package com.project.paymentservice.service;

import com.project.paymentservice.chapa.*;
import com.project.paymentservice.dto.PaymentReceiptDTO;
import com.project.paymentservice.dto.PaymentRequestDTO;
import com.project.paymentservice.feignclient.client.StudentServiceClient;
import com.project.paymentservice.feignclient.dto.StudentResponseDTO;
import com.project.paymentservice.model.*;
        import com.project.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ChapaClient chapaClient;
    private final PaymentRepository paymentRepository;
    private final StudentServiceClient studentServiceClient;
    private final CreditService creditService;
    private final CreditTransactionService creditTransactionService;

    @Transactional
    public List<Payment> processPayment(PaymentRequestDTO paymentRequestDTO) {
        List<Payment> paymentProtos = new ArrayList<>();
        BigDecimal totalRequiredAmount = BigDecimal.ZERO;
        String txRef = generateTxRef();

        // Assuming the amounts and offeringIDs lists are of the same size
        List<BigDecimal> amounts = paymentRequestDTO.getAmounts();
        List<Long> offeringIDs = paymentRequestDTO.getOfferingIDs();

        // Payments need to be saved first to get the payment id for possible credit transactions
        for (int i = 0; i < amounts.size(); i++) {
            Payment p = new Payment();
            p.setAmount(amounts.get(i));
            p.setOfferingID(offeringIDs.get(i)); // Set the offering ID
            p.setStudentId(paymentRequestDTO.getStudentId());
            p.setDescription(paymentRequestDTO.getDescription());
            p.setStatus(PaymentStatus.PENDING);
            p.setTxRef(txRef); // Shared txRef
            p.setType(PaymentType.DIRECT);

            paymentProtos.add(p);
            totalRequiredAmount = totalRequiredAmount.add(amounts.get(i));
        }

        List<Payment> payments = paymentRepository.saveAll(paymentProtos);

        Credit credit = creditService.getOrCreateCredit(paymentRequestDTO.getStudentId());
        BigDecimal creditBalance = credit.getBalance();
        BigDecimal requiredAmount;
        int creditOnly = 0;
        for (Payment payment : payments) {
            requiredAmount = payment.getAmount();
            if (creditBalance.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal creditUsed = creditBalance.min(requiredAmount);
                creditTransactionService.recordTransaction(paymentRequestDTO.getStudentId(), creditUsed, CreditTransactionType.CREDIT_USED, payment);
                totalRequiredAmount = totalRequiredAmount.subtract(creditUsed);
                requiredAmount = requiredAmount.subtract(creditUsed);
                creditBalance = creditBalance.subtract(creditUsed);

                if (requiredAmount.compareTo(BigDecimal.ZERO) > 0) {
                    payment.setType(PaymentType.PARTIAL_CREDIT);
                }
            }

            if (requiredAmount.compareTo(BigDecimal.ZERO) == 0) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setType(PaymentType.CREDIT_ONLY);
                creditOnly += 1;
            }
        }

        if (payments.size() == creditOnly) {
            return payments;
        }

        ChapaPaymentRequest chapaRequest = createChapaRequest(paymentRequestDTO, totalRequiredAmount, txRef);
        ChapaPaymentResponse chapaResponse = chapaClient.initializePayment(chapaRequest);

        if (chapaResponse == null || chapaResponse.getData() == null) {
            payments.stream().filter(p -> p.getType() != PaymentType.CREDIT_ONLY).forEach(p -> p.setStatus(PaymentStatus.FAILED));
        } else {
            payments.stream().filter(p -> p.getType() != PaymentType.CREDIT_ONLY).forEach(p -> p.setCheckoutUrl(chapaResponse.getData().getCheckout_url()));
        }

        return paymentRepository.saveAll(payments);
    }

    @Transactional
    public Payment verifyPayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found."));
        if (payment.getType().equals(PaymentType.CREDIT_ONLY)) {
            return payment;
        }
        ChapaVerificationResponse verificationResponse = chapaClient.verifyPayment(payment.getTxRef());

        if (verificationResponse == null || verificationResponse.getData() == null) {
            throw new RuntimeException("Failed to verify payment with Chapa.");
        }

        if (verificationResponse.getStatus().equals("success")) {
            payment.setStatus(verificationResponse.getData().getStatus().equalsIgnoreCase("success") ? PaymentStatus.SUCCESS : PaymentStatus.PENDING);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            throw new RuntimeException("Transaction not found with Chapa, payment marked as Failed.");
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public PaymentReceiptDTO verifyPayment(String txRef) {
        List<Payment> payments = paymentRepository.findByTxRef(txRef);
        if (payments.isEmpty()) {
            throw new RuntimeException("Payment not found with txRef: " + txRef);
        }

        Payment first_payment = payments.get(0);
//        if (first_payment.getType().equals(PaymentType.CREDIT_ONLY)) {
//            return first_payment;
//        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        ChapaVerificationResponse verificationResponse = chapaClient.verifyPayment(txRef);
        for (Payment payment : payments) {

            if (verificationResponse == null || verificationResponse.getData() == null) {
                throw new RuntimeException("Failed to verify payment with Chapa.");
            }

            if (verificationResponse.getStatus().equals("success")) {
                payment.setStatus(verificationResponse.getData().getStatus().equalsIgnoreCase("success") ? PaymentStatus.SUCCESS : PaymentStatus.PENDING);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                throw new RuntimeException("Transaction not found with Chapa, payment marked as Failed.");
            }

            paymentRepository.save(payment);
            totalAmount = totalAmount.add(payment.getAmount());
        }

        StudentResponseDTO student = studentServiceClient.getStudentById(first_payment.getStudentId()).getBody();
        return PaymentReceiptDTO.builder()
                .studentName(student.getName())
                .cardId(student.getCardId())
                .email(student.getEmail())
                .phone(student.getPhone())
                .description(first_payment.getDescription())
                .paymentDate(first_payment.getCreatedAt())
                .type(first_payment.getType())
                .totalFee(totalAmount)
                .status(first_payment.getStatus())
                .build();
    }

    @Transactional
    public Payment rollbackPayment(Long id, String reason) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found."));
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be rolled back.");
        }

        BigDecimal amount = payment.getAmount();
        Long studentId = payment.getStudentId();

        creditTransactionService.recordTransaction(studentId, amount, CreditTransactionType.CREDIT_ADDED, payment);

        payment.setStatus(PaymentStatus.ROLLED_BACK);
        payment.setRollbackReason(reason);

        return paymentRepository.save(payment);
    }

    private ChapaPaymentRequest createChapaRequest(PaymentRequestDTO dto, BigDecimal amount, String txRef) {
        StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
                .id(dto.getStudentId())
                .email("teststudent@email.com")
                .name("Test Student")
                .phone("+25199999999")
                .build();

        return ChapaPaymentRequest.builder()
                .currency("ETB")
                .email(studentResponseDTO.getEmail())
                .amount(amount.toString())
                .first_name(studentResponseDTO.getName().split(" ")[0])
                .last_name(studentResponseDTO.getName().split(" ")[1])
                .phone_number(studentResponseDTO.getPhone())
                .tx_ref(txRef)
                .customization(new Customization("HiLCoE", dto.getDescription()))
                .return_url("http://localhost:5173/payment/transition/" + txRef) // Use txRef in the return URL
                //.return_url("") // Use txRef in the return URL
                .build();
    }

    public String generateTxRef() {
        String txRef;
        do {
            txRef = "TX-" + UUID.randomUUID().toString().substring(0, 10);
        } while (!paymentRepository.findByTxRef(txRef).isEmpty());
        return txRef;
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found."));
    }

    public List<Payment> getPaymentByStudentId(Long id) {
        return paymentRepository.findByStudentId(id);
    }

    // Would subscribe to queue in pubsub
    @Transactional
    public void processWebhookEvent(Map<String, Object> payload) {
        String txRef = (String) payload.get("tx_ref");

        List<Payment> payments = paymentRepository.findByTxRef(txRef);
        if (payments.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with txRef:" + txRef);
        }

        payments.forEach(p -> verifyPayment(p.getTxRef()));
    }
}

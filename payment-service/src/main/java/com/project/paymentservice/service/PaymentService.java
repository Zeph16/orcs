package com.project.paymentservice.service;

import com.project.paymentservice.chapa.*;
        import com.project.paymentservice.dto.PaymentRequestDTO;
import com.project.paymentservice.feignclient.client.StudentServiceClient;
import com.project.paymentservice.feignclient.dto.StudentResponseDTO;
import com.project.paymentservice.model.*;
        import com.project.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public Payment processPayment(PaymentRequestDTO paymentRequestDTO) {
        Payment paymentProto = new Payment();
        paymentProto.setAmount(paymentRequestDTO.getAmount());
        paymentProto.setStudentId(paymentRequestDTO.getStudentId());
        paymentProto.setDescription(paymentRequestDTO.getDescription());
        paymentProto.setStatus(PaymentStatus.PENDING);
        paymentProto.setTxRef(generateTxRef());
        paymentProto.setType(PaymentType.DIRECT);
        Payment payment = paymentRepository.save(paymentProto);


        BigDecimal requiredAmount = paymentRequestDTO.getAmount();
        Credit credit = creditService.getOrCreateCredit(paymentRequestDTO.getStudentId());

        if (credit != null && credit.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal creditUsed = credit.getBalance().min(requiredAmount);
            creditTransactionService.recordTransaction(paymentRequestDTO.getStudentId(), creditUsed, CreditTransactionType.CREDIT_USED, payment);
            requiredAmount = requiredAmount.subtract(creditUsed);
        }

        if (requiredAmount.compareTo(BigDecimal.ZERO) == 0) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setType(PaymentType.CREDIT_ONLY);
            return paymentRepository.save(payment);
        }

        payment.setType(credit != null && credit.getBalance().compareTo(BigDecimal.ZERO) > 0 ? PaymentType.PARTIAL_CREDIT : PaymentType.DIRECT);
        ChapaPaymentRequest chapaRequest = createChapaRequest(paymentRequestDTO, requiredAmount, payment.getTxRef());
        ChapaPaymentResponse chapaResponse = chapaClient.initializePayment(chapaRequest);

        if (chapaResponse == null || chapaResponse.getData() == null) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Failed to initialize payment with Chapa.");
        }

        payment.setCheckoutUrl(chapaResponse.getData().getCheckout_url());
        return paymentRepository.save(payment);
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
        // StudentResponseDTO studentResponseDTO = studentServiceClient.getStudentById(dto.getStudentId()).getBody();

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
                .return_url("https://www.google.com")
                .build();
    }

    public String generateTxRef() {
        String txRef;
        do {
            txRef = "TX-" + UUID.randomUUID().toString().substring(0, 10);
        } while (paymentRepository.findByTxRef(txRef).isPresent());
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

        Payment payment = paymentRepository.findByTxRef(txRef)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        verifyPayment(payment.getId());
    }
}

package com.project.paymentservice.controller;
import com.project.paymentservice.dto.PaymentReceiptDTO;
import com.project.paymentservice.dto.PaymentRequestDTO;
import com.project.paymentservice.dto.PaymentPartialResponseDTO;
import com.project.paymentservice.dto.RollbackRequestDTO;
import com.project.paymentservice.model.Payment;
import com.project.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<List<PaymentPartialResponseDTO>> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        List<Payment> payments = paymentService.processPayment(paymentRequestDTO);
        List<PaymentPartialResponseDTO> responseDTOs = new ArrayList<>();
        payments.forEach(p -> responseDTOs.add(new PaymentPartialResponseDTO(p.getId(), p.getCheckoutUrl(), p.getType(), p.getStatus())));
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/verify/{txRef}")
    public ResponseEntity<PaymentReceiptDTO> verifyPayment(@PathVariable String txRef) {
        PaymentReceiptDTO payment = paymentService.verifyPayment(txRef);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/rollback")
    public ResponseEntity<Payment> rollbackPayment(@RequestBody RollbackRequestDTO rollbackRequestDTO) {
        Payment payment = paymentService.rollbackPayment(rollbackRequestDTO.getPaymentId(), rollbackRequestDTO.getReason());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<List<Payment>> getPaymentsByStudentId(@PathVariable Long id) {
        List<Payment> payments = paymentService.getPaymentByStudentId(id);
        return ResponseEntity.ok(payments);
    }
}

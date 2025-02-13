package com.project.paymentservice.controller;

import com.project.paymentservice.chapa.ChapaClient;
import com.project.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;
    private final ChapaClient chapaClient;
    private final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/chapa")
    public ResponseEntity<String> handleChapaWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Chapa-Signature", required = false) String xChapaSignature) {
        logger.info("Webhook received; Chapa signature: {}; Payload: {}", xChapaSignature, payload.toString());

        // Unnecessary because we're calling the chapa verify endpoint anyway
        // if (!chapaClient.isValidSignature(payload, xChapaSignature)) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        // }


        // Would instead publish to queue in pubsub
        paymentService.processWebhookEvent(payload);

        return ResponseEntity.ok("Webhook processed");
    }
}

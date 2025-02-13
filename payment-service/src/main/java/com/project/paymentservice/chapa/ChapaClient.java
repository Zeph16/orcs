package com.project.paymentservice.chapa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapaClient {

    @Value("${chapa.api.secretKey}")
    private String secretKey;

    private static final String BASE_URL = "https://api.chapa.co/v1";

    private final RestTemplate restTemplate;

    public ChapaPaymentResponse initializePayment(ChapaPaymentRequest paymentRequest) {
        String url = BASE_URL + "/transaction/initialize";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + secretKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<ChapaPaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<ChapaPaymentResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ChapaPaymentResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
//            throw new RuntimeException("Error initializing payment: " + response.getStatusCode());
            return null;
        }
    }

    public ChapaVerificationResponse verifyPayment(String txRef) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/transaction/verify/{tx_ref}")
                .buildAndExpand(txRef)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + secretKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<ChapaVerificationResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ChapaVerificationResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
//            throw new RuntimeException("Error verifying payment: " + response.getStatusCode());
            return null;
        }
    }

    public boolean isValidSignature(Map<String, Object> payload, String signature) {
        if (signature == null) return false;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            String payloadString = payload.toString();
            byte[] hashedBytes = sha256_HMAC.doFinal(payloadString.getBytes(StandardCharsets.UTF_8));
            String computedSignature = bytesToHex(hashedBytes);

            return computedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }


    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

}

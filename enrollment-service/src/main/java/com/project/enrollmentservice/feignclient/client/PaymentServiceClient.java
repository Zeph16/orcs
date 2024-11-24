package com.project.enrollmentservice.feignclient.client;

import com.project.enrollmentservice.feignclient.fallback.PaymentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service", fallback = PaymentServiceFallback.class)
public interface PaymentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}

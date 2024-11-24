package com.project.schedulingservice.feignclient.client;

import com.project.schedulingservice.feignclient.fallback.PaymentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service", fallback = PaymentServiceFallback.class)
public interface PaymentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}

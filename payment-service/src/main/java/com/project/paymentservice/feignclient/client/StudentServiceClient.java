package com.project.paymentservice.feignclient.client;

import com.project.paymentservice.feignclient.fallback.StudentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}
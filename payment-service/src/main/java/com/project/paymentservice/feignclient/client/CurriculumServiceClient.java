package com.project.paymentservice.feignclient.client;


import com.project.paymentservice.feignclient.fallback.CurriculumServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "curriculum-service", fallback = CurriculumServiceFallback.class)
public interface CurriculumServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}
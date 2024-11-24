package com.project.schedulingservice.feignclient.client;

import com.project.schedulingservice.feignclient.fallback.EnrollmentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "enrollment-service", fallback = EnrollmentServiceFallback.class)
public interface EnrollmentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}

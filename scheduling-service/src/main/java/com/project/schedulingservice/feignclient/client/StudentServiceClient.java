package com.project.schedulingservice.feignclient.client;

import com.project.schedulingservice.feignclient.fallback.StudentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}
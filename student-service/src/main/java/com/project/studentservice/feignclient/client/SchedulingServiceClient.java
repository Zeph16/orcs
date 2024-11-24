package com.project.studentservice.feignclient.client;

import com.project.studentservice.feignclient.fallback.SchedulingServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "scheduling-service", fallback = SchedulingServiceFallback.class)
public interface SchedulingServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}
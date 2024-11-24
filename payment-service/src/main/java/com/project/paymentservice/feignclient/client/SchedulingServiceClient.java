package com.project.paymentservice.feignclient.client;

import com.project.paymentservice.feignclient.fallback.SchedulingServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "scheduling-service", fallback = SchedulingServiceFallback.class)
public interface SchedulingServiceClient {

    @GetMapping("/demo/hi")
    String hi();
}

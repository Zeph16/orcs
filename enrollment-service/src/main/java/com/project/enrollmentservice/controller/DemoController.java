package com.project.enrollmentservice.controller;

import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.client.PaymentServiceClient;
import com.project.enrollmentservice.feignclient.client.SchedulingServiceClient;
import com.project.enrollmentservice.feignclient.client.StudentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {
    private final StudentServiceClient studentService;
    private final CurriculumServiceClient curriculumService;
    private final SchedulingServiceClient schedulingService;
    private final PaymentServiceClient paymentService;

    @GetMapping("/hi")
    public String hi() {
        return "Hi from enrollment-service!";
    }

    @GetMapping("/hi-student")
    public String hiStudent() {
        return "Enrollment service is calling student-service... - " + studentService.hi();
    }

    @GetMapping("/hi-curriculum")
    public String hiCurriculum() {
        return "Enrollment service is calling curriculum-service... - " + curriculumService.hi();
    }

    @GetMapping("/hi-scheduling")
    public String hiScheduling() {
        return "Enrollment service is calling scheduling-service... - " + schedulingService.hi();
    }

    @GetMapping("/hi-payment")
    public String hiPayment() {
        return "Enrollment service is calling payment-service... - " + paymentService.hi();
    }

}

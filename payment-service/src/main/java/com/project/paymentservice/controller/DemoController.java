package com.project.paymentservice.controller;

import com.project.paymentservice.feignclient.client.CurriculumServiceClient;
import com.project.paymentservice.feignclient.client.SchedulingServiceClient;
import com.project.paymentservice.feignclient.client.EnrollmentServiceClient;
import com.project.paymentservice.feignclient.client.StudentServiceClient;
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
    private final EnrollmentServiceClient enrollmentService;
    private final SchedulingServiceClient schedulingService;

    @GetMapping("/hi")
    public String hi() {
        return "Hi from payment-service!";
    }

    @GetMapping("/hi-student")
    public String hiStudent() {
        return "Payment service is calling student-service... - " + studentService.hi();
    }

    @GetMapping("/hi-curriculum")
    public String hiCurriculum() {
        return "Payment service is calling curriculum-service... - " + curriculumService.hi();
    }

    @GetMapping("/hi-enrollment")
    public String hiEnrollment() {
        return "Payment service is calling enrollment-service... - " + enrollmentService.hi();
    }

    @GetMapping("/hi-payment")
    public String hiScheduling() {
        return "Payment service is calling scheduling-service... - " + schedulingService.hi();
    }

}

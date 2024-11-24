package com.project.schedulingservice.controller;

import com.project.schedulingservice.feignclient.client.CurriculumServiceClient;
import com.project.schedulingservice.feignclient.client.PaymentServiceClient;
import com.project.schedulingservice.feignclient.client.EnrollmentServiceClient;
import com.project.schedulingservice.feignclient.client.StudentServiceClient;
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
    private final PaymentServiceClient paymentService;

    @GetMapping("/hi")
    public String hi() {
        return "Hi from scheduling-service!";
    }

    @GetMapping("/hi-student")
    public String hiStudent() {
        return "Scheduling service is calling student-service... - " + studentService.hi();
    }

    @GetMapping("/hi-curriculum")
    public String hiCurriculum() {
        return "Scheduling service is calling curriculum-service... - " + curriculumService.hi();
    }

    @GetMapping("/hi-enrollment")
    public String hiScheduling() {
        return "Scheduling service is calling enrollment-service... - " + enrollmentService.hi();
    }

    @GetMapping("/hi-payment")
    public String hiPayment() {
        return "Scheduling service is calling payment-service... - " + paymentService.hi();
    }

}

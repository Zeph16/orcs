package com.project.studentservice.controller;

import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.client.EnrollmentServiceClient;
import com.project.studentservice.feignclient.client.PaymentServiceClient;
import com.project.studentservice.feignclient.client.SchedulingServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final CurriculumServiceClient curriculumService;
    private final EnrollmentServiceClient enrollmentService;
    private final SchedulingServiceClient schedulingService;
    private final PaymentServiceClient paymentService;

    @GetMapping("/hi")
    public String hi() {
        return "Hi from student-service!";
    }

    @GetMapping("/hi-curriculum")
    public String hiCurriculum() {
        return "Student service is calling curriculum-service... - " + curriculumService.hi();
    }

    @GetMapping("/hi-enrollment")
    public String hiEnrollment() {
        return "Student service is calling enrollment-service... - " + enrollmentService.hi();
    }

    @GetMapping("/hi-scheduling")
    public String hiScheduling() {
        return "Student service is calling scheduling-service... - " + schedulingService.hi();
    }

    @GetMapping("/hi-payment")
    public String hiPayment() {
        return "Student service is calling payment-service... - " + paymentService.hi();
    }
}
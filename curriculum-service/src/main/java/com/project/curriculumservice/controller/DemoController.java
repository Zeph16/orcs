package com.project.curriculumservice.controller;

import com.project.curriculumservice.feignclient.client.EnrollmentServiceClient;
import com.project.curriculumservice.feignclient.client.PaymentServiceClient;
import com.project.curriculumservice.feignclient.client.SchedulingServiceClient;
import com.project.curriculumservice.feignclient.client.StudentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final StudentServiceClient studentService;
    private final EnrollmentServiceClient enrollmentService;
    private final SchedulingServiceClient schedulingService;
    private final PaymentServiceClient paymentService;



    @GetMapping("/hi")
    public String hi() {
        return "Hi from curriculum-service!";
    }

    @GetMapping("/hi-student")
    public String hiStudent() {
        return "Curriculum service is calling student-service... - " + studentService.hi();
    }


    @GetMapping("/hi-enrollment")
    public String hiEnrollment() {
        return "Curriculum service is calling enrollment-service... - " + enrollmentService.hi();
    }

    @GetMapping("/hi-scheduling")
    public String hiScheduling() {
        return "Curriculum service is calling scheduling-service... - " + schedulingService.hi();
    }

    @GetMapping("/hi-payment")
    public String hiPayment() {
        return "Curriculum service is calling payment-service... - " + paymentService.hi();
    }
}

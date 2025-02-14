package com.project.paymentservice.controller;

import com.project.paymentservice.feignclient.client.CurriculumServiceClient;
import com.project.paymentservice.feignclient.client.SchedulingServiceClient;
import com.project.paymentservice.feignclient.client.EnrollmentServiceClient;
import com.project.paymentservice.feignclient.client.StudentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('student')")
    @GetMapping("/auth")
    public String auth(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        // Return the username and roles as a string
        return "User: " + username + ", Roles: " + roles;
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

package com.project.studentservice.controller;

import com.project.studentservice.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakUserService keycloakUserService;

    @PostMapping("/create")
    public ResponseEntity<String> createUser() {
        String id = keycloakUserService.createUser("testuser2", "testuser2@email.com", "Test", "User");
        keycloakUserService.sendPasswordResetEmail(id);
        return ResponseEntity.ok(id);
    }
}

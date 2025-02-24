package com.project.studentservice.service;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class KeycloakUserService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String KEYCLOAK_BASE_URL = "http://localhost:8181";
    private static final String REALM = "HiLCoE";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String CLIENT_ID = "admin-cli";

    public String createUser(String username, String email, String firstName, String lastName) {
        String adminToken = getAdminAccessToken();

        if (adminToken == null) {
            throw new RuntimeException("Failed to get admin token");
        }

        String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> user = Map.of(
                "username", username,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            String location = response.getHeaders().getLocation().toString();
            String userId = location.substring(location.lastIndexOf("/") + 1);
            System.out.println("Created User ID: " + userId);
            return userId;
        } else {
            return null;
        }

    }

    private String getAdminAccessToken() {
        String url = KEYCLOAK_BASE_URL + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "client_id=" + CLIENT_ID +
                "&username=" + ADMIN_USERNAME +
                "&password=" + ADMIN_PASSWORD +
                "&grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
    }

    public void sendPasswordResetEmail(String userId) {
        String adminToken = getAdminAccessToken();

        if (adminToken == null) {
            throw new RuntimeException("Failed to get admin token");
        }
        String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users/" + userId + "/execute-actions-email";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("[\"UPDATE_PASSWORD\"]", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            System.out.println("Password reset email sent to user: " + userId);
        } else {
            System.out.println("Failed to send email: " + response.getBody());
        }
    }

}

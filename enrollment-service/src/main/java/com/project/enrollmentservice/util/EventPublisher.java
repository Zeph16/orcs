package com.project.enrollmentservice.util;

import com.project.enrollmentservice.dto.AddCourseRecommendationResponse;
import com.project.enrollmentservice.dto.EnrollmentRequestResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    // { id, name, cardId }
    public void publishAddRecommendationCreated(AddCourseRecommendationResponse rec) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "add_recommendation.created");
        event.put("data", Map.of("id", rec.getRecommendationID(),
                                "studentId", rec.getStudent().getId(),
                                "studentCardId", rec.getStudent().getCardId(),
                                "courseId", rec.getCourse().getCourseId(),
                                "courseTitle", rec.getCourse().getTitle(),
                                "courseCode", rec.getCourse().getCode(),
                                "termCode", rec.getTerm().getCode()));

        publish(event);
    }

    public void publishEnrollmentRequestApproved(EnrollmentRequestResponseDTO req) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "enrollment_request.approved");
        event.put("data", Map.of("id", req.getRequestID(),
                                "studentId", req.getStudent().getId(),
                                "studentCardId", req.getStudent().getCardId(),
                                "courseId", req.getCourseOffering().getCourse().getCourseId(),
                                "courseCode", req.getCourseOffering().getCourse().getCode(),
                                "courseTitle", req.getCourseOffering().getCourse().getTitle()));


        publish(event);
    }

    public void publishEnrollmentRequestDenied(EnrollmentRequestResponseDTO req) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "enrollment_request.denied");
        event.put("data", Map.of("id", req.getRequestID(),
                "studentId", req.getStudent().getId(),
                "studentCardId", req.getStudent().getCardId(),
                "courseId", req.getCourseOffering().getCourse().getCourseId(),
                "courseCode", req.getCourseOffering().getCourse().getCode(),
                "courseTitle", req.getCourseOffering().getCourse().getTitle()));


        publish(event);
        publish(event);
    }

    public void publish(Map<String, Object> event) {
        try {
            rabbitTemplate.convertAndSend("notification.exchange", "notifications", event);
            System.out.println("Published: " + event);
        } catch (Exception e) {
            // system execution shouldn't stop
            logger.error("Error when publishing event: {}", e.getMessage(), e);
        }
    }
}

package com.project.studentservice.util;

import com.project.studentservice.dto.AcademicRecordResponseDTO;
import com.project.studentservice.dto.StudentResponseDTO;
import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.model.Student;
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
    public void publishStudentCreated(StudentResponseDTO student) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "student.created");
        event.put("data", Map.of("id", student.getId(), "name", student.getName(), "studentCardId", student.getCardId()));

        publish(event);
    }

    // { id, courseCode, courseName, grade }
    public void publishRecordCreated(AcademicRecordResponseDTO record) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "record.created");
        event.put("data", Map.of("id", record.getId(), "studentCardId", record.getStudentCardId(), "courseCode", record.getCourseCode(), "courseName", record.getCourseName(), "grade", record.getGrade()));

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

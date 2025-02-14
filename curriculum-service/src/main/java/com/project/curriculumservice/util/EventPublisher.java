package com.project.curriculumservice.util;

import com.project.curriculumservice.dto.TermResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishNewTermCreated(TermResponseDTO termResponseDTO) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "term.created");
        event.put("data", Map.of("id", termResponseDTO.getTermId(), "code", termResponseDTO.getCode()));

        try {
            rabbitTemplate.convertAndSend("notification.exchange", "notifications", event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Published: " + event);
    }
}

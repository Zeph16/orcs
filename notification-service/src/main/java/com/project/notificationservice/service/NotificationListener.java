package com.project.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationListener {
    private final EventHandler eventHandler;
    public NotificationListener(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @RabbitListener(queues = "notification.queue")
    public void handleEvent(Map<String, Object> event) throws JsonProcessingException {
        String eventType = (String) event.get("type");
        Map<String, Object> eventData = (Map<String, Object>) event.get("data");

        // Optional, will be passed to handler functions that need them
        String userId = (String) eventData.get("userId");

        switch (eventType) {
            case "term.created":
                eventHandler.handleTermCreated(eventType, eventData);
                break;

            default:
                System.out.println("Unhandled event: " + eventType);
                break;
        }
    }

}

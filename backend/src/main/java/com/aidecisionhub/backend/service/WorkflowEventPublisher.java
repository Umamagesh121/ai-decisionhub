package com.aidecisionhub.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkflowEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WorkflowEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(UUID requestId, String stage, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("requestId", requestId);
        payload.put("stage", stage);
        payload.put("message", message);
        payload.put("timestamp", Instant.now().toString());
        messagingTemplate.convertAndSend("/topic/requests/" + requestId, payload);
    }
}

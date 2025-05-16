package com.API_Testing.APIx.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendNotification(String destination, Notification notification) {
        logger.info("Sending notification to {} : {}", destination, notification);
        simpMessagingTemplate.convertAndSend(destination, notification);
    }

    public void sendMessage(String destination, Object message) {
        logger.info("Sending Message to {} : {}", destination, message);
        simpMessagingTemplate.convertAndSend(destination, message);
    }
}

package com.API_Testing.APIx.websocket;

import com.API_Testing.APIx.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationServiceController(SimpMessagingTemplate simpMessagingTemplate, NotificationRepository notificationRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(String destination, Notification notification) {
        logger.info("Sending notification to {} : {}", destination, notification);
        simpMessagingTemplate.convertAndSend(destination, notification);
    }

    public void sendMessage(String destination, Object message) {
        logger.info("Sending Message to {} : {}", destination, message);
        simpMessagingTemplate.convertAndSend(destination, message);
        notificationRepository.save((Notification) message);
    }
}

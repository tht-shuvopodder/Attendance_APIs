package com.API_Testing.APIx.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RealtimeMessagingController {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessagingController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public RealtimeMessagingController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/messages")
    @SendTo("/topic/chat")
    public ResponseEntity<?> sendMessage(@Payload String message){
        logger.info("Message received: " + message);
        System.out.println("Message received: " + message);

        simpMessagingTemplate.convertAndSend("/topic/chat", message);
        return ResponseEntity.ok(message);
    }

    @MessageMapping("/notifications-send")
    @SendTo("/topic/notifications")
    public ResponseEntity<?> sendNotification(@Payload Notification notification){
       // simpMessagingTemplate.convertAndSend("/topic/notifications", notification);
        simpMessagingTemplate.convertAndSend(
                "/topic/notifications/"+notification.getReceiver().toString(),
                notification);
        return ResponseEntity.ok(notification);
    }
}

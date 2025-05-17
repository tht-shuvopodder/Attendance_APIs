package com.API_Testing.APIx.websocket;

import com.API_Testing.APIx.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class RealtimeMessagingController {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessagingController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    public RealtimeMessagingController(SimpMessagingTemplate simpMessagingTemplate, NotificationRepository notificationRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    @MessageMapping("/messages")
    @SendTo("/topic/chat")
    public ResponseEntity<?> sendMessage(@Payload String message){
        logger.info("Message received: " + message);
        System.out.println("Message received: " + message);

        simpMessagingTemplate.convertAndSend("/topic/chat", message);
        return ResponseEntity.ok(message);
    }

    @MessageMapping("/notification-seen")
    public void seenNotification(@Payload Notification notification){
        logger.info("Notification seen received: " + notification);
        notificationRepository.delete(notification);
    }

    @MessageMapping("/connect")
    public void connectUser(@Payload Map<String, String> payload){
        String mac = payload.get("mac");
        String employeeId = payload.get("employeeId");

        logger.info("Connecting user: " + mac);

        // search in db and resend to user
        List<Notification> notificationList = notificationRepository.findAllByMacAndReceiver(mac, employeeId);
        System.out.println("Notification Count: " + notificationList.size());
        for (Notification notification : notificationList) {
            //logger.info("Notification sending: " + notification);
            simpMessagingTemplate.convertAndSend("/topic/notifications/"+mac+"/"+employeeId, notification);
        }
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

package com.API_Testing.APIx.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private String title;
    private String content;
    private boolean read;
    private String receiver;
    private String type;
    @CreationTimestamp
    private LocalDateTime date;
}

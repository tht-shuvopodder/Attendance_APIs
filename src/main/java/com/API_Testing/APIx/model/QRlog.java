package com.API_Testing.APIx.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "qr_request_log")

public class QRlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    private String mac;
    private String employeeEmail;
    private String employeeId;
    private Timestamp generatedAt;
    private Timestamp expiresAt;
    @Column(name = "used")
    private boolean used = false;
}

package com.API_Testing.APIx.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Table(name = "device_info")



public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;


    @Column(name = "Device_Name")
    private String name;

    @Column(name = "MAC_Address",unique = true)
    private String deviceMAC;

    @Column(name = "Description")
    private String deviceDescription;

    @CreationTimestamp
    @Column(name = "Created_At",nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

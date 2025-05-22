package com.API_Testing.APIx.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "admin_device_map")
public class AdminDeviceMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Admin_name")
    private String adminName;

    @Column(name = "Admin_Email")
    private String adminEmail;

    @Column(name = "Device_MAC")
    private String deviceMAC;

}

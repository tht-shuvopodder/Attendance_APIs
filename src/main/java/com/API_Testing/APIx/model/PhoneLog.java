package com.API_Testing.APIx.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class PhoneLog {

    @Id
    @Column(name = "EMP_ID", nullable = false, unique = true)
    private String empId;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Device_MAC")
    private String deviceMAC;

    @Column(name = "Valid")
    private boolean valid;

}

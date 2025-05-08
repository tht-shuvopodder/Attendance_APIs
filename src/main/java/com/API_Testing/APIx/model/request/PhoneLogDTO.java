package com.API_Testing.APIx.model.request;

import jakarta.persistence.Column;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter

public class PhoneLogDTO {

    @Column(name = "EMP_ID", unique = true)
    private String empId;

    @Column(name = "Em@il", unique = true)
    private String email;

    @Column(name = "Device_MAC")
    private String deviceMAC;
}

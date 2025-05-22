package com.API_Testing.APIx.model.request;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdminDeviceAssignDTO {

    private String adminName;
    private String phone;
    private String adminEmail;
    private String deviceMAC;

}

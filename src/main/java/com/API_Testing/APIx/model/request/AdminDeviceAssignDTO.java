package com.API_Testing.APIx.model.request;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdminDeviceAssignDTO {

    private String name;
    private String phone;
    private String email;
    private String deviceMAC;

}

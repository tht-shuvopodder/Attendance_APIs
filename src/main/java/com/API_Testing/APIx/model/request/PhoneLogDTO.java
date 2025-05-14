package com.API_Testing.APIx.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter

public class PhoneLogDTO {

    private String empId;
    private String email;
    private String deviceMAC;
    private boolean valid;
}

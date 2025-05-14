package com.API_Testing.APIx.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class QRRequestDTO {
    private String deviceMAC;
    private String employeeEmail;
    private String employeeId;
}

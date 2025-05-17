package com.API_Testing.APIx.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter

public class DeviceDTO {

        @NotBlank(message = "Device name is required")
        private String deviceName;

        @NotBlank(message = "Device MAC is required")
        private String deviceMAC;
        private String deviceDescription;
        private boolean active;
        private LocalDateTime createdAt;

}

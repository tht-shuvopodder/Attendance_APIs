package com.API_Testing.APIx.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter

public class EmployeeDTO {
    @NotBlank(message = "MAC address is required and cannot be empty")
    private String deviceMAC;  // Mandatory field to target correct table
    private String deviceName;
    private String name;
    private String employeeId;
    private String designation;
    private String address;
    private String email;
    private String contactNumber;
    private float salary;
    private float overtimeRate;
    private String startDate;
    private String startTime;
    private String imageFile;
    private String employeeType;
    private String allowedAttendanceModes;
    private String allowedAttendanceActions;
    private String visibleDataTypes;
    private String addedBy;
    private List<Double> embedding;

}


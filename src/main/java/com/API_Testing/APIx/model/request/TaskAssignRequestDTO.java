package com.API_Testing.APIx.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@Getter
@Setter
public class TaskAssignRequestDTO {

    private Integer taskId;
    private List<String> employeeIds;
    private String deviceMac;
    private LocalDate lastDate;
    private String documentUrl;
    private Long adminId;

}

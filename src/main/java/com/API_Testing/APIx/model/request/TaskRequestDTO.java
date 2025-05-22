package com.API_Testing.APIx.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class TaskRequestDTO {

    private String title;
    private String taskDescription;
    private Long adminId;

}

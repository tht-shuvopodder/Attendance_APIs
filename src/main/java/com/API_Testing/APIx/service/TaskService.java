package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.TaskInfo;
import com.API_Testing.APIx.model.request.TaskAssignRequestDTO;
import com.API_Testing.APIx.model.request.TaskRequestDTO;


public interface TaskService {

    TaskInfo createTask(TaskRequestDTO request);
    void assignTask(TaskAssignRequestDTO request);

}

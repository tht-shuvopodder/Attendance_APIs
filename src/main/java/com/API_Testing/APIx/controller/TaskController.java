package com.API_Testing.APIx.controller;

import com.API_Testing.APIx.model.TaskInfo;
import com.API_Testing.APIx.model.request.TaskAssignRequestDTO;
import com.API_Testing.APIx.model.request.TaskRequestDTO;
import com.API_Testing.APIx.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTask(@RequestBody TaskRequestDTO request) {
        taskService.createTask(request);
        return ResponseEntity.ok("✅ New Task added to the list.");
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTask(@RequestBody TaskAssignRequestDTO request) {
        taskService.assignTask(request);
        return ResponseEntity.ok("✅ Task assigned successfully to selected employees.");
    }

}

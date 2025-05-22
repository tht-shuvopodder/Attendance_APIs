package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.Utility.DeviceEmployeeValidator;
import com.API_Testing.APIx.exception.GlobalExceptionHandler;
import com.API_Testing.APIx.model.TaskDistribution;
import com.API_Testing.APIx.model.TaskInfo;
import com.API_Testing.APIx.model.request.TaskAssignRequestDTO;
import com.API_Testing.APIx.model.request.TaskRequestDTO;
import com.API_Testing.APIx.repository.TaskDistributionRepo;
import com.API_Testing.APIx.repository.TaskInfoRepo;
import com.API_Testing.APIx.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class TaskImpl implements TaskService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final TaskInfoRepo taskInfoRepo;
    private final TaskDistributionRepo taskDistributionRepo;
    private final DeviceEmployeeValidator validator;

    public TaskImpl(TaskInfoRepo taskInfoRepo, TaskDistributionRepo taskDistributionRepo, DeviceEmployeeValidator validator) {
        this.taskInfoRepo = taskInfoRepo;
        this.taskDistributionRepo = taskDistributionRepo;
        this.validator = validator;
    }

    @Override
    public TaskInfo createTask(TaskRequestDTO request) {

        validator.validateAdminExists(request.getAdminId());

        String adminName = getAdminNameById(request.getAdminId());

        TaskInfo task = new TaskInfo();
        task.setTitle(request.getTitle());
        task.setTaskDescription(request.getTaskDescription());
        task.setAdminId(request.getAdminId());
        task.setAdminName(adminName);
        return taskInfoRepo.save(task);
    }

    @Override
    public void assignTask(TaskAssignRequestDTO request) {
        validator.validateDeviceExists(request.getDeviceMac());
        validator.validateAdminExists(request.getAdminId());
        validator.validateDeviceAndEmployees(request.getDeviceMac(), request.getEmployeeIds());
        validator.validateTaskExists(request.getTaskId());

        String tableName = validator.formatMacToTableName(request.getDeviceMac());
        String adminName = getAdminNameById(request.getAdminId());

        for (String empId : request.getEmployeeIds()) {
            String empName = getEmployeeNameById(tableName, empId);

            TaskDistribution dist = new TaskDistribution();
            dist.setTaskId(request.getTaskId());
            dist.setEmployeeId(empId);
            dist.setEmployeeName(empName);
            dist.setAdminId(request.getAdminId());
            dist.setAdminName(adminName);
            dist.setDeviceMAC(request.getDeviceMac());
            dist.setDocumentUrl(request.getDocumentUrl());
            dist.setLastDate(request.getLastDate());

            taskDistributionRepo.save(dist);
        }
    }

    private String getEmployeeNameById(String tableName, String empId) {
        String sql = "SELECT name FROM " + tableName + " WHERE employee_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, empId);
        } catch (EmptyResultDataAccessException e) {
            throw new GlobalExceptionHandler.EmployeeNotFoundException("Employee with ID " + empId + " not found.");
        }
    }

    private String getAdminNameById(Long adminId) {
        String sql = "SELECT admin_name FROM admin WHERE admin_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, adminId);
        } catch (EmptyResultDataAccessException e) {
            throw new GlobalExceptionHandler.AdminNotFoundException("Admin with ID " + adminId + " not found.");
        }
    }

}

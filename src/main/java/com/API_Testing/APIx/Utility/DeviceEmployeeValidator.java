package com.API_Testing.APIx.Utility;

import com.API_Testing.APIx.exception.GlobalExceptionHandler;
import com.API_Testing.APIx.model.Admin;
import com.API_Testing.APIx.repository.AdminRepo;
import com.API_Testing.APIx.repository.DeviceRepo;
import com.API_Testing.APIx.repository.TaskInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DeviceEmployeeValidator {

    @Autowired
    private final DeviceRepo deviceRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private TaskInfoRepo taskInfoRepo;
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public void validateDeviceExists(String deviceMAC) {

        if (!deviceRepo.existsByDeviceMAC(deviceMAC)) {
            throw new GlobalExceptionHandler.DeviceNotFoundException(" Device with MAC address '" + deviceMAC + "' not found.");
        }
    }

    public void validateAdminExists(Long adminId){

        if (!adminRepo.existsByAdminId(adminId)){
            throw new GlobalExceptionHandler.AdminNotFoundException(" Admin with ID: '" + adminId + "' not found..!");
        }

    }

    public void validateTaskExists(Integer taskId){

        if(!taskInfoRepo.existsByTaskId(taskId)) {
            throw new GlobalExceptionHandler.TaskNotFoundException(" Task with ID '" + taskId + "' not found..!");
        }
    }


    public void validateEmployeeExists(String deviceMAC, String empId) {
        String tableName = formatMacToTableName(deviceMAC);
        String sql = "SELECT COUNT(*) FROM `" + tableName + "` WHERE employee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, empId);
        if (count == null || count == 0) {
            throw new GlobalExceptionHandler.EmployeeNotFoundException("Employee with ID '" + empId + "' not found under device '" + deviceMAC + "'");
        }
    }

    public String formatMacToTableName(String mac) {
        return "device_" + mac.replace(":", "_").replace("-", "_");
    }

    public void validateDeviceAndEmployee(String deviceMAC, String empId) {
        validateDeviceExists(deviceMAC);
        validateEmployeeExists(deviceMAC, empId);
    }

    public void validateDeviceAndEmployees(String deviceMAC, List<String> employeeIds) {
        String tableName = formatMacToTableName(deviceMAC); // assuming you have this utility method

        for (String empId : employeeIds) {
            String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE employee_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, empId);

            if (count == null || count == 0) {
                throw new GlobalExceptionHandler.EmployeeNotFoundException(
                        "Employee with ID '" + empId + "' not found under device '" + deviceMAC + "'"
                );
            }
        }
    }

    public void validateAdminUniqueness(String name, String email, String phone) {
        Optional<Admin> optionalAdmin = adminRepo.findByAdminEmail(email);

        if (optionalAdmin.isPresent()) {
            Admin existingAdmin = optionalAdmin.get();

            // Same email is found, check if name and phone also match
            if (!existingAdmin.getAdminName().equals(name) || !existingAdmin.getPhone().equals(phone)) {
                throw new IllegalArgumentException("⚠️ Admin with email '" + email + "' already exists with a different name or phone.");
            }

            // All match — allow (could be assigning to another device)
            return;
        }

        // Also prevent someone from registering a new admin with same name but different email/phone
        Optional<Admin> byName = adminRepo.findByAdminName(name);
        if (byName.isPresent()) {
            Admin existingAdmin = byName.get();
            if (!existingAdmin.getAdminEmail().equals(email) || !existingAdmin.getPhone().equals(phone)) {
                throw new IllegalArgumentException("⚠️ Admin name '" + name + "' is already used with a different email or phone.");
            }
        }

        // Also check if phone is reused under different name or email
        Optional<Admin> byPhone = adminRepo.findByPhone(phone);
        if (byPhone.isPresent()) {
            Admin existingAdmin = byPhone.get();
            if (!existingAdmin.getAdminEmail().equals(email) || !existingAdmin.getAdminName().equals(name)) {
                throw new IllegalArgumentException("⚠️ Phone number '" + phone + "' is already used with a different admin.");
            }
        }
    }

}
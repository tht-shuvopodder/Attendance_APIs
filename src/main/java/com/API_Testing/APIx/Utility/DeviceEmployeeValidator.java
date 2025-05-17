package com.API_Testing.APIx.Utility;

import com.API_Testing.APIx.exception.GlobalExceptionHandler;
import com.API_Testing.APIx.model.Admin;
import com.API_Testing.APIx.repository.AdminRepo;
import com.API_Testing.APIx.repository.DeviceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DeviceEmployeeValidator {

    @Autowired
    private final DeviceRepo deviceRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public void validateDeviceExists(String deviceMAC) {

        if (!deviceRepo.existsByDeviceMAC(deviceMAC)) {
            throw new GlobalExceptionHandler.DeviceNotFoundException("Device with MAC address " + deviceMAC + " not found.");
        }
    }

    public void validateEmployeeExists(String deviceMAC, String empId) {
        String tableName = formatMacToTableName(deviceMAC);
        String sql = "SELECT COUNT(*) FROM `" + tableName + "` WHERE employee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, empId);
        if (count == null || count == 0) {
            throw new GlobalExceptionHandler.EmployeeNotFoundException("Employee ID " + empId + " not found under device " + deviceMAC);
        }
    }

    private String formatMacToTableName(String mac) {
        return "device_" + mac.replace(":", "_").replace("-", "_");
    }

    public void validateDeviceAndEmployee(String deviceMAC, String empId) {
        validateDeviceExists(deviceMAC);
        validateEmployeeExists(deviceMAC, empId);
    }

    public void validateAdminUniqueness(String name, String email, String phone) {
        Optional<Admin> optionalAdmin = adminRepo.findByEmail(email);

        if (optionalAdmin.isPresent()) {
            Admin existingAdmin = optionalAdmin.get();

            // Check if name and phone match
            if (!existingAdmin.getName().equals(name) || !existingAdmin.getPhone().equals(phone)) {
                throw new IllegalArgumentException("❌ Admin with email '" + email + "' already exists with different name or phone.");
            }

            // Valid existing admin; you can just return if no action needed
            //throw new IllegalArgumentException("⚠️ Admin with email '" + email + "' is already registered.");
        }
    }
}
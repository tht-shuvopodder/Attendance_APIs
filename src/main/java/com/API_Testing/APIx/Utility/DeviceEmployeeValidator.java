package com.API_Testing.APIx.Utility;

import com.API_Testing.APIx.exception.GlobalExceptionHandler;
import com.API_Testing.APIx.repository.DeviceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DeviceEmployeeValidator {

    private final DeviceRepo deviceRepo;
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
}

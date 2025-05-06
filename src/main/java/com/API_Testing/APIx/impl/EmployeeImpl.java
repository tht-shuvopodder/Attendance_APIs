package com.API_Testing.APIx.impl;


import com.API_Testing.APIx.model.request.EmployeeDTO;
import com.API_Testing.APIx.service.DeviceService;
import com.API_Testing.APIx.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeImpl implements EmployeeService{

    @Autowired
    private DeviceService deviceService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void addEmployeeToDevice(EmployeeDTO employee) {
        String tableName = formatMacToTableName(employee.getDeviceMAC());

        if (employee.getMacAddress() == null || employee.getMacAddress().isBlank()) {
            throw new IllegalArgumentException("MAC address is mandatory to add employee data.");
        }

        // ✅ Convert List<Double> embedding to JSON string
        String embeddingJson;
        try {
            embeddingJson = objectMapper.writeValueAsString(employee.getEmbedding());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert embedding to JSON", e);
        }

        String mac = employee.getMacAddress();
        // ✅ Check if the target device exists
        if (!deviceService.existsByDeviceMAC(mac)) {
            throw new IllegalArgumentException("Device with MAC " + mac + " does not exist. Please register the device first.");
        }

        try {
            String jsonEmbedding = objectMapper.writeValueAsString(employee.getEmbedding());
            String insertSQL = String.format(
                    "INSERT INTO %s (name, employee_id, designation, address, email, contact_number, device_MAC, salary, " +
                            "overtime_rate, start_date, start_time, image_file, employee_type, allowed_attendance_modes, " +
                            "allowed_attendance_actions, visible_data_types, added_by, embedding) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    tableName
            );

            jdbcTemplate.update(insertSQL,
                    employee.getName(),
                    employee.getEmployeeId(),
                    employee.getDesignation(),
                    employee.getAddress(),
                    employee.getEmail(),
                    employee.getContactNumber(),
                    mac,
                    employee.getSalary(),
                    employee.getOvertimeRate(),
                    employee.getStartDate(),
                    employee.getStartTime(),
                    employee.getImageFile(),
                    employee.getEmployeeType(),
                    employee.getAllowedAttendanceModes(),
                    employee.getAllowedAttendanceActions(),
                    employee.getVisibleDataTypes(),
                    employee.getAddedBy(),
                    embeddingJson
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize embedding list to JSON", e);
        }
    }

    public String formatMacToTableName(String deviceMAC) {
        return "device_" + deviceMAC.replace(":", "_").replace("-", "_");
    }


    @Override
    public void partialUpdateEmployee(String macAddress, String employeeId, Map<String, Object> updates) {
        String tableName = formatMacToTableName(macAddress);

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Object> params = new ArrayList<>();

        updates.forEach((key, value) -> {
            sql.append(key).append(" = ?, ");
            params.add(value);
        });

        sql.setLength(sql.length() - 2); // remove last comma
        sql.append(" WHERE employee_id = ?");
        params.add(employeeId);

        int rows = jdbcTemplate.update(sql.toString(), params.toArray());
        if (rows == 0) {
            throw new RuntimeException("Employee not found or update failed.");
        }
    }

    @Override
    public void deleteEmployee(String macAddress, String employeeId) {
        String tableName = formatMacToTableName(macAddress);
        String sql = "DELETE FROM " + tableName + " WHERE employee_id = ?";
        int rows = jdbcTemplate.update(sql, employeeId);
        if (rows == 0) {
            throw new RuntimeException("Employee not found or already deleted.");
        }
    }

    @Override
    public List<EmployeeDTO> getAllEmployeesByDeviceMAC(String deviceMAC) {
        String tableName = formatMacToTableName(deviceMAC);

        String sql = String.format("SELECT * FROM %s", tableName);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setMacAddress(deviceMAC);
            dto.setDeviceMAC(rs.getString("device_MAC"));
            dto.setName(rs.getString("name"));
            dto.setEmployeeId(rs.getString("employee_id"));
            dto.setDesignation(rs.getString("designation"));
            dto.setAddress(rs.getString("address"));
            dto.setEmail(rs.getString("email"));
            dto.setContactNumber(rs.getString("contact_number"));
            dto.setSalary(rs.getFloat("salary"));
            dto.setOvertimeRate(rs.getFloat("overtime_rate"));
            dto.setStartDate(rs.getString("start_date"));
            dto.setStartTime(rs.getString("start_time"));
            dto.setImageFile(rs.getString("image_file"));
            dto.setEmployeeType(rs.getString("employee_type"));
            dto.setAllowedAttendanceModes(rs.getString("allowed_attendance_modes"));
            dto.setAllowedAttendanceActions(rs.getString("allowed_attendance_actions"));
            dto.setVisibleDataTypes(rs.getString("visible_data_types"));
            dto.setAddedBy(rs.getString("added_by"));

            // Handle embedding JSON string
            try {
                String embeddingJson = rs.getString("embedding");
                List<Double> embeddingList = objectMapper.readValue(embeddingJson, new TypeReference<List<Double>>() {});
                dto.setEmbedding(embeddingList);
            } catch (Exception e) {
                dto.setEmbedding(Collections.emptyList()); // Or log error
            }

            return dto;
        });
    }


}

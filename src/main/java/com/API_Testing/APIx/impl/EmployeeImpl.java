package com.API_Testing.APIx.impl;


import com.API_Testing.APIx.model.request.EmployeeDTO;
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
    ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void addEmployeeToDevice(EmployeeDTO employee) {
        String tableName = formatMacToTableName(employee.getDeviceMAC());

        if (employee.getDeviceMAC() == null || employee.getDeviceMAC().isBlank()) {
            throw new IllegalArgumentException("MAC address is mandatory to add employee data.");
        }

        // ✅ Convert List<Double> embedding to JSON string
        String embeddingJson;
        try {
            embeddingJson = objectMapper.writeValueAsString(employee.getEmbedding());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert embedding to JSON", e);
        }

        try {
            String jsonEmbedding = objectMapper.writeValueAsString(employee.getEmbedding());
            String insertSQL = String.format(
                    "INSERT INTO %s (name, employee_id, designation, address, email, contact_number, device_MAC, device_name, salary, " +
                            "overtime_rate, start_date, start_time, image_file, employee_type, allowed_attendance_modes, " +
                            "allowed_attendance_actions, visible_data_types, added_by, embedding) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    tableName
            );

            jdbcTemplate.update(insertSQL,
                    employee.getName(),
                    employee.getEmployeeId(),
                    employee.getDesignation(),
                    employee.getAddress(),
                    employee.getEmail(),
                    employee.getContactNumber(),
                    employee.getDeviceMAC(),
                    employee.getDeviceName(),
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

        Map<String, String> fieldToColumn = Map.ofEntries(
                Map.entry("name", "name"),
                Map.entry("designation", "designation"),
                Map.entry("address", "address"),
                Map.entry("email", "email"),
                Map.entry("contactNumber", "contact_number"),
                Map.entry("salary", "salary"),
                Map.entry("overtimeRate", "overtime_rate"),
                Map.entry("startDate", "start_date"),
                Map.entry("startTime", "start_time"),
                Map.entry("imageFile", "image_file"),
                Map.entry("employeeType", "employee_type"),
                Map.entry("allowedAttendanceModes", "allowed_attendance_modes"),
                Map.entry("allowedAttendanceActions", "allowed_attendance_actions"),
                Map.entry("visibleDataTypes", "visible_data_types"),
                Map.entry("addedBy", "added_by"),
                Map.entry("embedding", "embedding"),
                Map.entry("employeeId", "employee_id"),
                Map.entry("macAddress", "device_mac"),
                Map.entry("deviceName", "device_name")
        );

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            String column = fieldToColumn.get(field);
            if (column == null || value == null) continue; // Skip unknown or null

            sql.append(column).append(" = ?, ");

            if ("embedding".equals(field)) {
                try {
                    String jsonEmbedding = objectMapper.writeValueAsString(value);
                    params.add(jsonEmbedding);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to serialize embedding list to JSON", e);
                }
            } else {
                params.add(value);
            }
        }

        if (params.isEmpty()) {
            throw new RuntimeException("No valid fields to update.");
        }

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
            dto.setDeviceMAC(rs.getString("device_MAC"));
            dto.setDeviceName(rs.getString("device_name"));
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

    @Override
    public boolean existsByDeviceMACAndEmployeeId(String macAddress, String employeeId) {
        String tableName = formatMacToTableName(macAddress);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE employee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, employeeId);
        return count != null && count > 0;
    }


}

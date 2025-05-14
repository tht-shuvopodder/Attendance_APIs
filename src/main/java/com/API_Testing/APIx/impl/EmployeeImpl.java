package com.API_Testing.APIx.impl;


import com.API_Testing.APIx.model.request.EmployeeDTO;
import com.API_Testing.APIx.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeImpl implements EmployeeService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> FIELD_TO_COLUMN = Map.ofEntries(
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

    private String formatMacToTableName(String macAddress) {
        return "device_" + macAddress.replace(":", "_").replace("-", "_");
    }

    @Override
    public void addEmployeeToDevice(EmployeeDTO employee) {
        if (employee.getDeviceMAC() == null || employee.getDeviceMAC().isBlank()) {
            throw new IllegalArgumentException("MAC address is mandatory to add employee data.");
        }
        String tableName = formatMacToTableName(employee.getDeviceMAC());
        String embeddingJson = serializeEmbedding(employee.getEmbedding());

        String sql = String.format("INSERT INTO %s (name, employee_id, designation, address, email, contact_number, " +
                "device_MAC, device_name, salary, overtime_rate, start_date, start_time, image_file, employee_type, " +
                "allowed_attendance_modes, allowed_attendance_actions, visible_data_types, added_by, embedding) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", tableName);

        jdbcTemplate.update(sql,
                employee.getName(), employee.getEmployeeId(), employee.getDesignation(), employee.getAddress(),
                employee.getEmail(), employee.getContactNumber(), employee.getDeviceMAC(), employee.getDeviceName(),
                employee.getSalary(), employee.getOvertimeRate(), employee.getStartDate(), employee.getStartTime(),
                employee.getImageFile(), employee.getEmployeeType(), employee.getAllowedAttendanceModes(),
                employee.getAllowedAttendanceActions(), employee.getVisibleDataTypes(), employee.getAddedBy(), embeddingJson);
    }

    @Override
    public void partialUpdateEmployee(String macAddress, String employeeId, Map<String, Object> updates) {
        String tableName = formatMacToTableName(macAddress);
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String column = FIELD_TO_COLUMN.get(entry.getKey());
            if (column == null || entry.getValue() == null) continue;

            sql.append(column).append(" = ?, ");
            if ("embedding".equals(entry.getKey())) {
                params.add(serializeEmbedding(entry.getValue()));
            } else {
                params.add(entry.getValue());
            }
        }

        if (params.isEmpty()) throw new RuntimeException("No valid fields to update.");

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE employee_id = ?");
        params.add(employeeId);

        if (jdbcTemplate.update(sql.toString(), params.toArray()) == 0) {
            throw new RuntimeException("Employee not found or update failed.");
        }
    }

    @Override
    public String deleteEmployee(String macAddress, String employeeId) {
        String tableName = formatMacToTableName(macAddress);
        int rows = jdbcTemplate.update("DELETE FROM " + tableName + " WHERE employee_id = ?", employeeId);

        StringBuilder result = new StringBuilder();

        if (rows == 0) {
            result.append("Employee not found..!");
        } else {
            result.append("✅ Employee deleted successfully ");
        }
        int logRows = jdbcTemplate.update("DELETE FROM phone_log WHERE emp_id = ?", employeeId);


        if (logRows == 0) {
            result.append("⚠️ but Phone Log not found for deletion.");
        } else {
            result.append("with the Phone Log.");
        }

        return result.toString();
    }

    @Override
    public List<EmployeeDTO> getAllEmployeesByDeviceMAC(String deviceMAC) {
        String tableName = formatMacToTableName(deviceMAC);
        return jdbcTemplate.query("SELECT * FROM " + tableName, (rs, rowNum) -> {
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

            try {
                dto.setEmbedding(objectMapper.readValue(rs.getString("embedding"), new TypeReference<>() {}));
            } catch (Exception e) {
                dto.setEmbedding(Collections.emptyList());
            }

            return dto;
        });
    }

    @Override
    public boolean existsByDeviceMACAndEmployeeId(String macAddress, String employeeId) {
        String tableName = formatMacToTableName(macAddress);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName + " WHERE employee_id = ?",
                Integer.class, employeeId);
        return count != null && count > 0;
    }

    private String serializeEmbedding(Object embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize embedding", e);
        }
    }
}

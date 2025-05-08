package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.request.EmployeeDTO;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    void addEmployeeToDevice(EmployeeDTO employee);

    void partialUpdateEmployee(String macAddress, String employeeId, Map<String, Object> updates);

    void deleteEmployee(String macAddress, String employeeId);

    List<EmployeeDTO> getAllEmployeesByDeviceMAC(String deviceMAC);

    boolean existsByDeviceMACAndEmployeeId(String macAddress, String employeeId);

}

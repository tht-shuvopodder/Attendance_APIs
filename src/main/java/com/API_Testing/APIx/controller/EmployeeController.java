package com.API_Testing.APIx.controller;


import com.API_Testing.APIx.Utility.DeviceEmployeeValidator;
import com.API_Testing.APIx.exception.GlobalExceptionHandler;
import com.API_Testing.APIx.model.request.EmployeeDTO;
import com.API_Testing.APIx.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private DeviceEmployeeValidator validator;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeeDTO employee) {
        if (employee.getDeviceMAC() == null || employee.getDeviceMAC().isBlank()) {
            throw new GlobalExceptionHandler.InvalidRequestException("MAC address is mandatory to add employee data.");
        }
        validator.validateDeviceExists(employee.getDeviceMAC());
        if (employeeService.existsByDeviceMACAndEmployeeId(employee.getDeviceMAC(), employee.getEmployeeId())) {
            throw new GlobalExceptionHandler.EmployeeAlreadyExistsException("Employee already exists under this device.");
        }
        employeeService.addEmployeeToDevice(employee);
        return ResponseEntity.ok("✅ Employee added successfully.");
    }

    @PatchMapping("/update/{mac}/{id}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable String mac,
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        validator.validateDeviceExists(mac);
        validator.validateEmployeeExists(mac, id);
        employeeService.partialUpdateEmployee(mac, id, updates);
        return ResponseEntity.ok("✅ Employee updated successfully.");
    }

    @DeleteMapping("/delete/{mac}/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String mac, @PathVariable String id) {
        validator.validateDeviceExists(mac);
        validator.validateEmployeeExists(mac, id);
        return ResponseEntity.ok(employeeService.deleteEmployee(mac, id));
    }

    @GetMapping("/all/{mac}")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@PathVariable String mac) {
        validator.validateDeviceExists(mac);
        return ResponseEntity.ok(employeeService.getAllEmployeesByDeviceMAC(mac));
    }

}



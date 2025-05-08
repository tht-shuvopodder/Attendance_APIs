package com.API_Testing.APIx.controller;


import com.API_Testing.APIx.model.request.EmployeeDTO;
import com.API_Testing.APIx.service.DeviceService;
import com.API_Testing.APIx.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<String> addEmployeeToDevice(@RequestBody EmployeeDTO employee) {
        try {
            if (employee.getDeviceMAC() == null || employee.getDeviceMAC().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⛔ MAC address is mandatory to add employee data.");
            }


            if (!deviceService.existsByDeviceMAC(employee.getDeviceMAC())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⛔ Device with MAC [" + (employee.getDeviceMAC()) + "] does not exist. Please register the device first.");
            }


            if (employeeService.existsByDeviceMACAndEmployeeId(employee.getDeviceMAC(), employee.getEmployeeId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ Employee with ID [" + (employee.getEmployeeId()) + "] already exists under device [" + (employee.getDeviceMAC()) + "].");
            }


            employeeService.addEmployeeToDevice(employee);
            return ResponseEntity.ok("✅ Employee added successfully under device MAC [" + employee.getDeviceMAC() + "].");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "⛔ Failed to add employee: " + e.getMessage()
            );
        }
    }


    @PatchMapping("/update/{mac}/{employeeId}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable String mac,
            @PathVariable String employeeId,
            @RequestBody Map<String, Object> updates) {
        try {
            employeeService.partialUpdateEmployee(mac, employeeId, updates);
            return ResponseEntity.ok("✅ Employee updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⛔ Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{mac}/{employeeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable String mac,
            @PathVariable String employeeId) {
        try {
            employeeService.deleteEmployee(mac, employeeId);
            return ResponseEntity.ok("✅ Employee deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⛔ Deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/all/{deviceMAC}")
    public ResponseEntity<?> getEmployeesByDeviceMAC(@PathVariable String deviceMAC) {
        try {
            List<EmployeeDTO> employees = employeeService.getAllEmployeesByDeviceMAC(deviceMAC);

            if (employees == null || employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⛔ No employees found for device MAC: " + deviceMAC);
            }

            return ResponseEntity.ok(employees);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⛔ Invalid device MAC address: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⛔ Server error while retrieving employees: " + e.getMessage());
        }
    }

}



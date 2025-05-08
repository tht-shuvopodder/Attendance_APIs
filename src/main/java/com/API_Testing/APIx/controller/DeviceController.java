package com.API_Testing.APIx.controller;

import com.API_Testing.APIx.model.Device;
import com.API_Testing.APIx.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/devices")

public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/store")
    public ResponseEntity<String> saveDevice(@Valid @RequestBody Device device) {
        try {
            if (deviceService.existsByDeviceMAC(device.getDeviceMAC())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⛔ Device with MAC " + device.getDeviceMAC() + " is already registered.");
            }

            deviceService.create(device);
            return ResponseEntity.ok("✅ New Device added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⛔ Failed to register device: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/exists/{deviceMAC}")
    public ResponseEntity<String> checkDeviceExists(@PathVariable String deviceMAC) {
        boolean exists = deviceService.existsByDeviceMAC(deviceMAC);
        if (exists) {
            return ResponseEntity.ok("Device exists. ✅");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    String.format("⛔ No device found with MAC address [%s]. You may proceed to register it.", deviceMAC)
            );
        }
    }

    @DeleteMapping("/delete/{macAddress}")
    public ResponseEntity<String> deleteDevice(@PathVariable String macAddress) {
        try {
            // Format table name
            String tableName = "device_" + macAddress.replace(":", "_");

            // Drop the dynamic device table
            String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
            jdbcTemplate.execute(dropTableQuery);

            // Remove from device_info table
            String deleteDeviceInfoQuery = "DELETE FROM device_info WHERE mac_address = ?";
            int rowsAffected = jdbcTemplate.update(deleteDeviceInfoQuery, macAddress);

            if (rowsAffected > 0) {
                return ResponseEntity.ok("✅ Device and it's table deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("⛔ No such device found.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("⛔ Error deleting device: " + e.getMessage());
        }
    }

}

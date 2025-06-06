package com.API_Testing.APIx.controller;


import com.API_Testing.APIx.model.Device;
import com.API_Testing.APIx.model.request.DeviceDTO;
import com.API_Testing.APIx.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/devices")

public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @PostMapping("/store")
    public ResponseEntity<String> saveDevice(@Valid @RequestBody DeviceDTO dto) {
        if (deviceService.existsByDeviceMAC(dto.getDeviceMAC())) {
            throw new IllegalArgumentException("Device with MAC " + dto.getDeviceMAC() + " is already registered.");
        }
        // Map DTO → Entity
        Device device = deviceService.mapToDevice(dto);

        // Create dynamic employee table
        deviceService.create(device);

        return ResponseEntity.ok("✅ New Device added successfully.");
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
            throw new NoSuchElementException("No device found with MAC address [" + deviceMAC + "].");
        }
    }

    @DeleteMapping("/delete/{macAddress}")
    public ResponseEntity<String> deleteDevice(@PathVariable String macAddress) {
        String tableName = deviceService.formatMacToTableName(macAddress);

        // Drop table and delete device record
        int rowsAffected = deviceService.deleteDevice(macAddress, tableName);

        if (rowsAffected > 0) {
            return ResponseEntity.ok("✅ Device and Employee table deleted successfully.");
        } else {
            throw new NoSuchElementException("No such device found..!");
        }
    }


}

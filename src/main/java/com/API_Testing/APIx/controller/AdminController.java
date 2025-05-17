package com.API_Testing.APIx.controller;


import com.API_Testing.APIx.model.request.AdminDeviceAssignDTO;
import com.API_Testing.APIx.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/assign-device")
    public ResponseEntity<String> assignAdminToDevice(@RequestBody AdminDeviceAssignDTO dto) {
        String response = adminService.assignAdminToDevice(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admins-by-device")
    public ResponseEntity<List<String>> getAdminsByDeviceMAC(@RequestParam String mac) {
        List<String> adminEmails = adminService.getAdminEmailsByDeviceMAC(mac);
        return ResponseEntity.ok(adminEmails);
    }

    @GetMapping("/devices-by-admin")
    public ResponseEntity<List<String>> getDevicesByAdminEmail(@RequestParam String email) {
        List<String> deviceMacs = adminService.getDeviceMACsByAdminEmail(email);
        return ResponseEntity.ok(deviceMacs);
    }

}

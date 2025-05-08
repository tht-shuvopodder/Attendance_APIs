package com.API_Testing.APIx.controller;

import com.API_Testing.APIx.model.PhoneLog;
import com.API_Testing.APIx.model.request.PhoneLogDTO;
import com.API_Testing.APIx.service.PhoneLogService;
import com.API_Testing.APIx.service.QRlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping ("/phoneLog")
public class PhoneLogController {

    @Autowired
    PhoneLogService phoneLogService;

    @PostMapping("/save")
    public ResponseEntity<String> savePhoneLog (@Valid @RequestBody PhoneLog phoneLog){
        try {
            if(phoneLogService.existsByEmpIdAndDeviceMACAndEmailAndValidIsTrue(phoneLog.getEmpId(), phoneLog.getDeviceMAC(), phoneLog.getEmail())){
                return  ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⛔ This Employee with ID " + (phoneLog.getEmpId()) + " is already registered.");
            }

            phoneLogService.save(phoneLog);
            return ResponseEntity.ok("✅ Phone Log added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⛔ Failed : " + e.getMessage());
        }

    }

    @GetMapping("/get/{key}")
    public ResponseEntity<?> getPhoneLog(@PathVariable String key) {
        try {
            PhoneLogDTO dto = phoneLogService.getPhoneLog(key);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ No data found for: " + key);
        }
    }
}

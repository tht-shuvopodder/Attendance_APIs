package com.API_Testing.APIx.controller;


import com.API_Testing.APIx.model.request.AttendanceLogDTO;
import com.API_Testing.APIx.service.AttendanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceLogController {

    @Autowired
    AttendanceLogService attendanceLogService;

    @PostMapping("/punch")
    public ResponseEntity<String> punchAttendance(@RequestBody AttendanceLogDTO dto) {
        String result = attendanceLogService.punchAttendance(dto);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/date")
    public ResponseEntity<List<AttendanceLogDTO>> getAttendanceByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceLogDTO> logs = attendanceLogService.getByDate(date);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/emp")
    public ResponseEntity<AttendanceLogDTO> getAttendanceByEmpAndDate(
            @RequestParam String empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AttendanceLogDTO dto = attendanceLogService.getByEmpAndDate(empId, date);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/attendance-by-device")
    public ResponseEntity<List<AttendanceLogDTO>> getAllAttendanceByEmpAndDevice(
            @RequestParam String deviceMAC,
            @RequestParam String empId) {

        List<AttendanceLogDTO> logs = attendanceLogService.getAllByDeviceMacAndEmpId(deviceMAC, empId);
        return ResponseEntity.ok(logs);
    }



}

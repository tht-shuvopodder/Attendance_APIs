package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.request.AttendanceLogDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLogService {

    String punchAttendance(AttendanceLogDTO dto);
    List<AttendanceLogDTO> getByDate(LocalDate date);
    AttendanceLogDTO getByEmpAndDate(String empId, LocalDate date);
    List<AttendanceLogDTO> getAllByDeviceMacAndEmpId(String deviceMAC, String empId);
}

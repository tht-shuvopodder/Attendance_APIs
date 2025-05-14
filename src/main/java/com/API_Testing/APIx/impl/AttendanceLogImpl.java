package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.Utility.DeviceEmployeeValidator;
import com.API_Testing.APIx.model.AttendanceLog;
import com.API_Testing.APIx.model.request.AttendanceLogDTO;
import com.API_Testing.APIx.repository.AttendanceLogRepo;
import com.API_Testing.APIx.service.AttendanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceLogImpl  implements AttendanceLogService {

    @Autowired
    AttendanceLogRepo attendanceLogRepo;

    @Autowired
    DeviceEmployeeValidator validator;


    @Override
    public String punchAttendance(AttendanceLogDTO dto) {

        validator.validateDeviceAndEmployee(dto.getMacID(), dto.getEmpId());

        Optional<AttendanceLog> optional = attendanceLogRepo.findByEmpIdAndDate(
                dto.getEmpId(), dto.getDate());

        AttendanceLog log = optional.orElseGet(() -> {
            AttendanceLog newLog = new AttendanceLog();
            newLog.setEmpId(dto.getEmpId());
            newLog.setMacID(dto.getMacID());
            newLog.setDate(dto.getDate());
            return newLog;
        });

        if (dto.getCheckIn() != null && log.getCheckIn() == null)
            log.setCheckIn(dto.getCheckIn());
        else if (dto.getCheckIn() != null)
            return "⚠️ Check-in already recorded.";

        if (dto.getLunchTimeCheckIn() != null && log.getLunchTimeCheckIn() == null)
            log.setLunchTimeCheckIn(dto.getLunchTimeCheckIn());
        else if (dto.getLunchTimeCheckIn() != null)
            return "⚠️ Lunch check-in already recorded.";

        if (dto.getLunchTimeCheckOut() != null && log.getLunchTimeCheckOut() == null)
            log.setLunchTimeCheckOut(dto.getLunchTimeCheckOut());
        else if (dto.getLunchTimeCheckOut() != null)
            return "⚠️ Lunch check-out already recorded.";

        if (dto.getCheckOut() != null && log.getCheckOut() == null)
            log.setCheckOut(dto.getCheckOut());
        else if (dto.getCheckOut() != null)
            return "⚠️ Check-out already recorded.";

        if (dto.getStatus() != null)
            log.setStatus(dto.getStatus());

        attendanceLogRepo.save(log);
        return "✅ Attendance updated successfully.";
    }


    @Override
    public List<AttendanceLogDTO> getByDate(LocalDate date) {
        return attendanceLogRepo.findAllByDate(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceLogDTO getByEmpAndDate(String empId, LocalDate date) {
        AttendanceLog log = attendanceLogRepo.findByEmpIdAndDate(empId, date)
                .orElseThrow(() -> new NoSuchElementException("Data not found..!"));
        return mapToDTO(log);
    }

    private AttendanceLogDTO mapToDTO(AttendanceLog log) {
        AttendanceLogDTO dto = new AttendanceLogDTO();
        dto.setEmpId(log.getEmpId());
        dto.setMacID(log.getMacID());
        dto.setDate(log.getDate());
        dto.setCheckIn(log.getCheckIn());
        dto.setLunchTimeCheckIn(log.getLunchTimeCheckIn());
        dto.setLunchTimeCheckOut(log.getLunchTimeCheckOut());
        dto.setCheckOut(log.getCheckOut());
        dto.setStatus(log.getStatus());
        return dto;
    }


}


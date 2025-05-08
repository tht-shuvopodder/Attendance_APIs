package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.model.PhoneLog;
import com.API_Testing.APIx.model.request.PhoneLogDTO;
import com.API_Testing.APIx.repository.PhoneLogRepo;
import com.API_Testing.APIx.service.PhoneLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PhoneLogImpl implements PhoneLogService {

    @Autowired
    PhoneLogRepo phoneLogRepo;

    @Override
    public PhoneLog save(PhoneLog phoneLog){
        if (phoneLogRepo.findByEmail(phoneLog.getEmail()).isPresent()) {
            throw new IllegalArgumentException("❌ This email is already registered.");
        }

        PhoneLog log = new PhoneLog();
        log.setEmpId(phoneLog.getEmpId());
        log.setEmail(phoneLog.getEmail());
        log.setDeviceMAC(phoneLog.getDeviceMAC());
        log.setValid(true);
        return phoneLogRepo.save(phoneLog);
    }

    @Override
    public boolean existsByEmpIdAndDeviceMACAndEmailAndValidIsTrue(String empId, String deviceMAC, String email){
        return phoneLogRepo.existsByEmpIdAndDeviceMACAndEmailAndValidIsTrue(empId,deviceMAC,email);
    }

    @Override
    public PhoneLogDTO getPhoneLog(String key) {
        Optional<PhoneLog> optionalLog = phoneLogRepo.findByEmpId(key);

        if (optionalLog.isEmpty())
            optionalLog = phoneLogRepo.findByEmail(key);

        return optionalLog
                .map(this::mapToDTO)
                .orElseThrow(() -> new NoSuchElementException("No record found for key: " + key));
    }

    @Override
    public PhoneLogDTO mapToDTO(PhoneLog log) {
        PhoneLogDTO dto = new PhoneLogDTO();
        dto.setEmpId(log.getEmpId());
        dto.setEmail(log.getEmail());
        dto.setDeviceMAC(log.getDeviceMAC());
        return dto;
    }
}

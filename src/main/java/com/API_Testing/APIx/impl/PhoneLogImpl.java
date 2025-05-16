package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.model.PhoneLog;
import com.API_Testing.APIx.model.request.PhoneLogDTO;
import com.API_Testing.APIx.repository.PhoneLogRepo;
import com.API_Testing.APIx.service.PhoneLogService;
import com.API_Testing.APIx.websocket.Notification;
import com.API_Testing.APIx.websocket.NotificationServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PhoneLogImpl implements PhoneLogService {

    @Autowired
    PhoneLogRepo phoneLogRepo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final NotificationServiceController notificationServiceController;

    public PhoneLogImpl(NotificationServiceController notificationServiceController) {
        this.notificationServiceController = notificationServiceController;
    }

    @Override
    public PhoneLog save(PhoneLog phoneLog){
        // Check for existing phone log by email
        if (phoneLogRepo.findByEmail(phoneLog.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is already registered.");
        }

        // Validate employee existence based on device MAC sub-table
        String deviceTable = formatMacToTableName(phoneLog.getDeviceMAC());
        String sql = "SELECT COUNT(*) FROM " + deviceTable + " WHERE employee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phoneLog.getEmpId());

        if (count == null || count == 0) {
            throw new IllegalArgumentException("Employee ID does not exist under device " + phoneLog.getDeviceMAC());
        }

        // Save phone log
        PhoneLog log = new PhoneLog();
        log.setEmpId(phoneLog.getEmpId());
        log.setEmail(phoneLog.getEmail());
        log.setDeviceMAC(phoneLog.getDeviceMAC());
        log.setValid(true);
        PhoneLog saved = phoneLogRepo.save(log);


        Notification notification = new Notification();
        notification.setTitle("Welcome ");
        notification.setRead(false);
        notification.setContent("Welcome message");
        notification.setReceiver(phoneLog.getEmpId());
        notification.setType("SYSTEM");
        System.out.println("/topic/notifications/"+phoneLog.getDeviceMAC()+"/"+phoneLog.getEmpId());
        //simpMessagingTemplate.convertAndSend("/topic/notifications/"+macAddress+"/"+employeeId, notification);

        notification.setMac(phoneLog.getDeviceMAC());
        notificationServiceController.sendMessage("/topic/notifications/"+phoneLog.getDeviceMAC()+"/"+phoneLog.getEmpId(), notification);
        return saved;
    }

    public String formatMacToTableName(String deviceMAC) {
        return "device_" + deviceMAC.replace(":", "_").replace("-", "_");
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
        dto.setValid(log.isValid());
        return dto;
    }

    @Override
    public String updateValid(String email, boolean value) {
        PhoneLog log = phoneLogRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No record found for email: " + email));

        if (log.isValid() == value) {
            return "⚠️ 'valid' is already set to " + value + " for email: " + email;
        }

        log.setValid(value);
        phoneLogRepo.save(log);
        return "✅ 'valid' field updated to " + value + " for email: " + email;
    }


    @Override
    public void deletePhoneLog(String deviceMAC, String empId) {
        String tableName = "phone_log";
        String sql = "DELETE FROM " + tableName + " WHERE emp_id = ?";
        int rows = jdbcTemplate.update(sql, empId);
        if (rows == 0) {
            throw new RuntimeException("⚠️ Employee not found or already deleted.");
        }
    }


}

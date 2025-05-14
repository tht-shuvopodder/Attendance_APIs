package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.PhoneLog;
import com.API_Testing.APIx.model.request.PhoneLogDTO;

public interface PhoneLogService {

    PhoneLog save(PhoneLog phoneLog);

    boolean existsByEmpIdAndDeviceMACAndEmailAndValidIsTrue(String empId, String deviceMAC, String email);

    PhoneLogDTO getPhoneLog(String key);

    PhoneLogDTO mapToDTO (PhoneLog log);

    String updateValid(String email, boolean value);

    void deletePhoneLog(String deviceMAC, String empId);

}

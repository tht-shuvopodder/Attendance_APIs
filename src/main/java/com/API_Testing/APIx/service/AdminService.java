package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.request.AdminDeviceAssignDTO;

import java.util.List;

public interface AdminService {

    String assignAdminToDevice(AdminDeviceAssignDTO dto);

    List<String> getDeviceMACsByAdminEmail(String email);

    List<String> getAdminEmailsByDeviceMAC(String deviceMAC);

}

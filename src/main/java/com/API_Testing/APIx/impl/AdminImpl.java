package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.Utility.DeviceEmployeeValidator;
import com.API_Testing.APIx.model.Admin;
import com.API_Testing.APIx.model.AdminDeviceMap;
import com.API_Testing.APIx.model.request.AdminDeviceAssignDTO;
import com.API_Testing.APIx.repository.AdminDeviceMapRepo;
import com.API_Testing.APIx.repository.AdminRepo;
import com.API_Testing.APIx.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminImpl implements AdminService {

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    AdminDeviceMapRepo mapRepo;

    @Autowired
    DeviceEmployeeValidator validator;

    @Override
    public String assignAdminToDevice(AdminDeviceAssignDTO dto) {
        // ✅ Check if dev exists
        validator.validateDeviceExists(dto.getDeviceMAC());
        validator.validateAdminUniqueness(dto.getAdminName(), dto.getAdminEmail(), dto.getPhone());

        // ✅ Check if admin already exists by email
        Admin admin = adminRepo.findByAdminEmail(dto.getAdminEmail()).orElseGet(() -> {
            Admin newAdmin = new Admin();
            newAdmin.setAdminName(dto.getAdminName());
            newAdmin.setPhone(dto.getPhone());
            newAdmin.setAdminEmail(dto.getAdminEmail());
            return adminRepo.save(newAdmin);
        });

        boolean alreadyMapped = mapRepo.existsByAdminEmailAndDeviceMAC(admin.getAdminEmail(), dto.getDeviceMAC());
        if (alreadyMapped) {
            return "⚠️ Admin is already assigned to this device..!";
        }

        // ✅ Create the mapping
        AdminDeviceMap map = new AdminDeviceMap();
        map.setAdminName(admin.getAdminName());
        map.setAdminEmail(admin.getAdminEmail());
        map.setDeviceMAC(dto.getDeviceMAC());
        mapRepo.save(map);

        return "✅ Admin assigned to device successfully.";
    }

    @Override
    public List<String> getDeviceMACsByAdminEmail(String email) {
        Optional<Admin> optionalAdmin = adminRepo.findByAdminEmail(email);
        if (optionalAdmin.isEmpty()) {
            throw new IllegalArgumentException("Admin with email " + email + " not found..!");
        }

        List<AdminDeviceMap> mappings = mapRepo.findByAdminEmail(email);
        return mappings.stream()
                .map(AdminDeviceMap::getDeviceMAC)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAdminEmailsByDeviceMAC(String deviceMAC) {
        validator.validateDeviceExists(deviceMAC);
        List<AdminDeviceMap> mappings = mapRepo.findByDeviceMAC(deviceMAC);

        return mappings.stream()
                .map(AdminDeviceMap::getAdminEmail)
                .distinct()
                .collect(Collectors.toList());

    }


}

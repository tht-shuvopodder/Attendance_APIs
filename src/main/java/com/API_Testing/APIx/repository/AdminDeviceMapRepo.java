package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.Admin;
import com.API_Testing.APIx.model.AdminDeviceMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminDeviceMapRepo extends JpaRepository<AdminDeviceMap, Long> {

    List<AdminDeviceMap> findByAdminEmail(String adminEmail);
    List<AdminDeviceMap> findByDeviceMAC(String deviceMAC);
    boolean existsByAdminEmailAndDeviceMAC(String adminEmail, String deviceMAC);

}

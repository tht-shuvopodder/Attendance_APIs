package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepo extends JpaRepository<Device, Integer> {

    boolean existsByDeviceMAC(String deviceMAC);

}

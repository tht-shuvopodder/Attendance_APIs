package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.Device;
import com.API_Testing.APIx.model.request.DeviceDTO;
import jakarta.validation.Valid;

import java.util.List;


public interface DeviceService {

    void create(Device device);

    List<Device> getAllDevices();

    boolean existsByDeviceMAC(String macAddress);

    int deleteDevice(String macAddress, String tableName);

    String formatMacToTableName(String mac);

    Device mapToDevice(@Valid DeviceDTO dto);

}

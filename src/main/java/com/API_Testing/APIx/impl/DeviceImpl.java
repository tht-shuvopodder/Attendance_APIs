package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.model.Device;
import com.API_Testing.APIx.repository.DeviceRepo;
import com.API_Testing.APIx.service.DeviceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class DeviceImpl implements DeviceService {

    @Autowired
    DeviceRepo deviceRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void create(Device device) {
        /*if (deviceRepo.existsByDeviceMAC(device.getDeviceMAC())){
            throw new IllegalArgumentException("Device already registered..!");
        }*/

        // Save device to central table
        Device savedDevice = deviceRepo.save(device);

        String tableName = formatMacToTableName(device.getDeviceMAC());

        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "employee_id VARCHAR(255) NOT NULL UNIQUE, " +
                        "designation VARCHAR(255), " +
                        "address VARCHAR(255), " +
                        "email VARCHAR(255) NOT NULL UNIQUE, " +
                        "contact_number VARCHAR(255), " +
                        "device_MAC VARCHAR(255), " +
                        "device_name VARCHAR(255)," +
                        "salary FLOAT, " +
                        "overtime_rate FLOAT, " +
                        "start_date VARCHAR(255), " +
                        "start_time VARCHAR(255), " +
                        "image_file LONGTEXT, " +
                        "employee_type VARCHAR(255), " +
                        "allowed_attendance_modes VARCHAR(255), " +
                        "allowed_attendance_actions VARCHAR(255), " +
                        "visible_data_types VARCHAR(255), " +
                        "added_by VARCHAR(255)," +
                        "embedding LONGTEXT" +
                        ")", tableName);

        jdbcTemplate.execute(createTableSQL);

    }

    public String formatMacToTableName(String deviceMAC) {
        return "device_" + deviceMAC.replace(":", "_").replace("-", "_");
    }


    @Override
    public List<Device> getAllDevices(){
        entityManager.clear();
        return deviceRepo.findAll();
    }

    @Override
    public boolean  existsByDeviceMAC(String deviceMAC){
        return deviceRepo.existsByDeviceMAC(deviceMAC);
    }


}

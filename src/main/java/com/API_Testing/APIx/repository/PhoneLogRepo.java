package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.PhoneLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneLogRepo extends JpaRepository<PhoneLog, String> {

    boolean existsByEmpIdAndDeviceMACAndEmailAndValidIsTrue(String empId, String deviceMAC, String email);

    Optional<PhoneLog> findByEmail(String email);
    Optional<PhoneLog> findByEmpId(String empId);

}

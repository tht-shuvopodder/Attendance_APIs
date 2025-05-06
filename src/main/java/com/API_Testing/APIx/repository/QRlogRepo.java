package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.QRlog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface QRlogRepo extends JpaRepository<QRlog, Long> {

    Optional<QRlog> findByUuid(UUID uuid);

    boolean existsByMacAndEmployeeEmailAndUsedIsTrue(String mac, String email);

}

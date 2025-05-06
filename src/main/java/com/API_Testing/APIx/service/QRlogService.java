package com.API_Testing.APIx.service;

import com.API_Testing.APIx.model.QRlog;
import java.util.Optional;
import java.util.UUID;

public interface QRlogService {

    QRlog save(QRlog log);

    Optional<QRlog> findByUUID(UUID uuid);

    boolean existsByMacAndEmployeeEmailAndUsedIsTrue(String mac, String email);

}

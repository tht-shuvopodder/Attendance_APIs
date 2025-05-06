package com.API_Testing.APIx.impl;

import com.API_Testing.APIx.model.QRlog;
import com.API_Testing.APIx.repository.QRlogRepo;
import com.API_Testing.APIx.service.QRlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class QRlogImpl implements QRlogService {

    @Autowired
    private QRlogRepo qrlogRepo;

    @Override
    public QRlog save(QRlog log) {
        return qrlogRepo.save(log);
    }

    @Override
    public Optional<QRlog> findByUUID(UUID uuid) {
        return qrlogRepo.findByUuid(uuid);
    }

    @Override
    public boolean existsByMacAndEmployeeEmailAndUsedIsTrue(String mac, String email){
        return qrlogRepo.existsByMacAndEmployeeEmailAndUsedIsTrue(mac,email);
    }

}



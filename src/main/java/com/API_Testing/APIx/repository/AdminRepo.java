package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepo extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminEmail(String email);

    Optional<Admin> findByPhone(String phone);

    Optional<Admin> findByAdminName(String adminName);

    boolean existsByAdminId(long adminId);

}

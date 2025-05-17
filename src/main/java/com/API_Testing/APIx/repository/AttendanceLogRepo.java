package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepo extends JpaRepository<AttendanceLog, String> {

    List<AttendanceLog> findAllByDate(LocalDate date);
    Optional<AttendanceLog> findByEmpIdAndDate(String empId, LocalDate date);
    List<AttendanceLog> findByEmpIdAndMacID(String empId, String macID);


}

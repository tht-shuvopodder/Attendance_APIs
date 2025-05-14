package com.API_Testing.APIx.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Getter
@Setter
@Table(name = "attendance_log")

public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_ID", nullable = false)
    private String empId;

    @Column(name = "mac_ID", nullable = false)
    private String macID;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "lunchTime_checkIn")
    private LocalTime lunchTimeCheckIn;

    @Column(name = "lunchTime_checkOut")
    private LocalTime lunchTimeCheckOut;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Column(name = "status")
    private String status;
}

package com.API_Testing.APIx.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter

public class AttendanceLogDTO {

        private String empId;
        private String macID;
        private LocalDate date;
        private LocalTime checkIn;
        private LocalTime lunchTimeCheckIn;
        private LocalTime lunchTimeCheckOut;
        private LocalTime checkOut;
        private String status;
}



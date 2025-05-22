package com.API_Testing.APIx.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Admin_ID")
    private Long adminId;

    @Column(name = "Admin_name", unique = true)
    private String adminName;

    @Column(name = "Email", unique = true)
    private String adminEmail;

    @Column(name = "Contact_No", unique = true)
    private String phone;

}

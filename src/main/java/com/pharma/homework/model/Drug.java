package com.pharma.homework.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String manufacturer;

    // not sure with the format of batchNumber, need to confirm with BA, currently using String is more flexible
    private String batchNumber;

    private LocalDate expiryDate;

    private Integer stock;
}

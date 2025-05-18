package com.pharma.homework.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pharmacy")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    // init set firstly to avoid npe
    private Set<PharmacyDrugInfo> drugInfoSet = new HashSet<>();

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Set<PharmacyDrugInfo> getDrugInfoSet() {
        return drugInfoSet;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDrugInfoSet(Set<PharmacyDrugInfo> drugInfoSet) {
        this.drugInfoSet = drugInfoSet;
    }
}


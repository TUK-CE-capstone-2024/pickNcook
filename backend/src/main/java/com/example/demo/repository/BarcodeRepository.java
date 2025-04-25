package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Barcode;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, String> {
}


package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Barcode;
import com.example.demo.repository.BarcodeRepository;

@Service
public class BarcodeService {
    @Autowired
    private BarcodeRepository barcodeRepository;

    public Barcode getProductByBarcode(String barcodeNum) {
        return barcodeRepository.findById(barcodeNum).orElse(null);
    }
}

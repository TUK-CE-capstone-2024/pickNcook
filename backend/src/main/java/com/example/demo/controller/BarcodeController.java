package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Barcode;
import com.example.demo.service.BarcodeService;

@RestController
@RequestMapping("/api/barcode")
public class BarcodeController {
    @Autowired
    private BarcodeService barcodeService;

    @GetMapping("/{barcodeNum}")
    public ResponseEntity<Barcode> getProduct(@PathVariable("barcodeNum") String barcodeNum) {
    	System.out.println("바코드 요청 수신: " + barcodeNum);
        Barcode barcode = barcodeService.getProductByBarcode(barcodeNum);
        
        if (barcode != null) {
            System.out.println("조회된 상품 정보: " + barcode.getIngredientName());
            return ResponseEntity.ok(barcode);
        } else {
            System.out.println("바코드 정보 없음: " + barcodeNum);
            return ResponseEntity.notFound().build();
        //Barcode barcode = barcodeService.getProductByBarcode(barcodeNum);
        //return barcode != null ? ResponseEntity.ok(barcode) : ResponseEntity.notFound().build();
        }
    }
}
package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "barcode")
public class Barcode {
    @Id
    @Column(name = "barcode_num", length = 13)
    private String barcodeNum;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(name = "price", nullable = false)
    private int price;

    // Getter & Setter
    public String getBarcodeNum() {
        return barcodeNum;
    }

    public void setBarcodeNum(String barcodeNum) {
        this.barcodeNum = barcodeNum;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

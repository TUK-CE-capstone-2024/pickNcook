package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fridge")
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fridge_ingredient_no")
    private Integer fridgeIngredientNo;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "fridge_ingredient", nullable = false)
    private String fridgeIngredient;

    
    @Column(name = "photo")
    private byte[] photo;          // 사진 (nullable)


    public Integer getFridgeIngredientNo() {
        return fridgeIngredientNo;
    }

    public void setFridgeIngredientNo(Integer fridgeIngredientNo) {
        this.fridgeIngredientNo = fridgeIngredientNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFridgeIngredient() {
        return fridgeIngredient;
    }

    public void setFridgeIngredient(String fridgeIngredient) {
        this.fridgeIngredient = fridgeIngredient;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
}

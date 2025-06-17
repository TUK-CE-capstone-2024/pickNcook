package com.example.demo.model;

public class FridgeDTO {
    private Integer fridgeIngredientNo;
    private String userId;
    private String fridgeIngredient;

    

    // Getter/Setter
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
}

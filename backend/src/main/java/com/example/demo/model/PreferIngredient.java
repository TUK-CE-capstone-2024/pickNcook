package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "prefer_ingredient")
public class PreferIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prefer_ingredient_no")
    private Integer preferIngredientNo;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "prefer_ingredient")
    private String preferIngredient;

    // Getter / Setter
    public Integer getPreferIngredientNo() {
        return preferIngredientNo;
    }

    public void setPreferIngredientNo(Integer preferIngredientNo) {
        this.preferIngredientNo = preferIngredientNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPreferIngredient() {
        return preferIngredient;
    }

    public void setPreferIngredient(String preferIngredient) {
        this.preferIngredient = preferIngredient;
    }
}

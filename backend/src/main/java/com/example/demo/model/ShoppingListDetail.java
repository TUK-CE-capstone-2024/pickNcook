package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shopping_list_detail")
public class ShoppingListDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_detail_no")
    private Integer listDetailNo;

    @Column(name = "list_no")
    private Integer listNo;

    @Column(name = "ingredient")
    private String ingredient;

    // Getter & Setter

    public Integer getListDetailNo() {
        return listDetailNo;
    }

    public void setListDetailNo(Integer listDetailNo) {
        this.listDetailNo = listDetailNo;
    }

    public Integer getListNo() {
        return listNo;
    }

    public void setListNo(Integer listNo) {
        this.listNo = listNo;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}

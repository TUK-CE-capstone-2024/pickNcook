package com.example.demo.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "recipe_storage")
public class RecipeStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_no")
    private Integer storageNo;

    @Column(name = "recipe_no")
    private Integer recipeNo;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(name = "reg_date", updatable = false)
    private Timestamp regDate;

    // Getter & Setter
    public Integer getStorageNo() {
        return storageNo;
    }

    public void setStorageNo(Integer storageNo) {
        this.storageNo = storageNo;
    }

    public Integer getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(Integer recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getRegDate() {
        return regDate;
    }

    public void setRegDate(Timestamp regDate) {
        this.regDate = regDate;
    }
}

package com.example.demo.model;

public class SavedRecipeDTO {
    private int recipeNo;
    private String ckgNm;

    // 생성자
    public SavedRecipeDTO(int recipeNo, String ckgNm) {
        this.recipeNo = recipeNo;
        this.ckgNm = ckgNm;
    }

    // Getter & Setter
    public int getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(int recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getCkgNm() {
        return ckgNm;
    }

    public void setCkgNm(String ckgNm) {
        this.ckgNm = ckgNm;
    }
}

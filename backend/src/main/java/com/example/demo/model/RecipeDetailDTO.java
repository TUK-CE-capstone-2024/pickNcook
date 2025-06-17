package com.example.demo.model;


public class RecipeDetailDTO {
    private Integer recipeNo;
    private String rcpImgUrl;
    private String ckgNm;
    private String ckgInbunNm;
    private String ckgDodfNm;
    private String ckgTimeNm;
    private String ckgKndActoNm;
    private String ckgStaActoNm;
    private String ckgMthActoNm;

    public RecipeDetailDTO(Recipe recipe) {
        this.recipeNo = recipe.getRecipeNo();
        this.rcpImgUrl = recipe.getRcpImgUrl();
        this.ckgNm = recipe.getCkgNm();
        this.ckgInbunNm = recipe.getCkgInbunNm();
        this.ckgDodfNm = recipe.getCkgDodfNm();
        this.ckgTimeNm = recipe.getCkgTimeNm();
        this.ckgKndActoNm = recipe.getCkgKndActoNm();
        this.ckgStaActoNm = recipe.getCkgStaActoNm();
        this.ckgMthActoNm = recipe.getCkgMthActoNm();
    }

    // Getters  Setters
    public Integer getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(Integer recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getRcpImgUrl() {
        return rcpImgUrl;
    }

    public void setRcpImgUrl(String rcpImgUrl) {
        this.rcpImgUrl = rcpImgUrl;
    }

    public String getCkgNm() {
        return ckgNm;
    }

    public void setCkgNm(String ckgNm) {
        this.ckgNm = ckgNm;
    }

    public String getCkgInbunNm() {
        return ckgInbunNm;
    }

    public void setCkgInbunNm(String ckgInbunNm) {
        this.ckgInbunNm = ckgInbunNm;
    }

    public String getCkgDodfNm() {
        return ckgDodfNm;
    }

    public void setCkgDodfNm(String ckgDodfNm) {
        this.ckgDodfNm = ckgDodfNm;
    }

    public String getCkgTimeNm() {
        return ckgTimeNm;
    }

    public void setCkgTimeNm(String ckgTimeNm) {
        this.ckgTimeNm = ckgTimeNm;
    }

    public String getCkgKndActoNm() {
        return ckgKndActoNm;
    }

    public void setCkgKndActoNm(String ckgKndActoNm) {
        this.ckgKndActoNm = ckgKndActoNm;
    }

    public String getCkgStaActoNm() {
        return ckgStaActoNm;
    }

    public void setCkgStaActoNm(String ckgStaActoNm) {
        this.ckgStaActoNm = ckgStaActoNm;
    }

    public String getCkgMthActoNm() {
        return ckgMthActoNm;
    }

    public void setCkgMthActoNm(String ckgMthActoNm) {
        this.ckgMthActoNm = ckgMthActoNm;
    }
}

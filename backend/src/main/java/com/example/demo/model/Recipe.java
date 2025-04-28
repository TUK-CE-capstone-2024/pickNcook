package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_no")
    private Integer recipeNo;

    @Column(name = "rcp_ttl")
    private String rcpTtl;

    @Column(name = "ckg_nm")
    private String ckgNm;

    @Column(name = "ckg_mth_acto_nm")
    private String ckgMthActoNm;

    @Column(name = "ckg_sta_acto_nm")
    private String ckgStaActoNm;

    @Column(name = "ckg_mtrl_acto_nm")
    private String ckgMtrlActoNm;

    @Column(name = "ckg_knd_acto_nm")
    private String ckgKndActoNm;

    @Column(name = "ckg_mtrl_cn")
    private String ckgMtrlCn;

    @Column(name = "ckg_inbun_nm")
    private String ckgInbunNm;

    @Column(name = "ckg_dodf_nm")
    private String ckgDodfNm;

    @Column(name = "ckg_time_nm")
    private String ckgTimeNm;

    @Column(name = "rcp_img_url")
    private String rcpImgUrl;



    public Integer getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(Integer recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getRcpTtl() {
        return rcpTtl;
    }

    public void setRcpTtl(String rcpTtl) {
        this.rcpTtl = rcpTtl;
    }

    public String getCkgNm() {
        return ckgNm;
    }

    public void setCkgNm(String ckgNm) {
        this.ckgNm = ckgNm;
    }

    public String getCkgMthActoNm() {
        return ckgMthActoNm;
    }

    public void setCkgMthActoNm(String ckgMthActoNm) {
        this.ckgMthActoNm = ckgMthActoNm;
    }

    public String getCkgStaActoNm() {
        return ckgStaActoNm;
    }

    public void setCkgStaActoNm(String ckgStaActoNm) {
        this.ckgStaActoNm = ckgStaActoNm;
    }

    public String getCkgMtrlActoNm() {
        return ckgMtrlActoNm;
    }

    public void setCkgMtrlActoNm(String ckgMtrlActoNm) {
        this.ckgMtrlActoNm = ckgMtrlActoNm;
    }

    public String getCkgKndActoNm() {
        return ckgKndActoNm;
    }

    public void setCkgKndActoNm(String ckgKndActoNm) {
        this.ckgKndActoNm = ckgKndActoNm;
    }

    public String getCkgMtrlCn() {
        return ckgMtrlCn;
    }

    public void setCkgMtrlCn(String ckgMtrlCn) {
        this.ckgMtrlCn = ckgMtrlCn;
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

    public String getRcpImgUrl() {
        return rcpImgUrl;
    }

    public void setRcpImgUrl(String rcpImgUrl) {
        this.rcpImgUrl = rcpImgUrl;
    }
}

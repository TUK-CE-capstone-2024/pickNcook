package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_no")
    private Long cartItemNo;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    // Getter / Setter
    public Long getCartItemNo() {
        return cartItemNo;
    }
    public void setCartItemNo(Long cartItemNo) {
        this.cartItemNo = cartItemNo;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}

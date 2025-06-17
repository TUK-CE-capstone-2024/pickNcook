package com.example.demo.model;

import java.util.List;

public class RemovedFavoriteCheckRequest {
    private String userId;
    private List<String> previousFridgeItems;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getPreviousFridgeItems() {
        return previousFridgeItems;
    }

    public void setPreviousFridgeItems(List<String> previousFridgeItems) {
        this.previousFridgeItems = previousFridgeItems;
    }
}
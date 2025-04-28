package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Barcode;
import com.example.demo.model.ShoppingCart;
import com.example.demo.service.ShoppingCartService;

// 장바구니 요청 처리
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 장바구니에 아이템 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartRequest request) {
        Map<String, String> response = new HashMap<>();
        
        // 요청 데이터 검증
        if (request.getUserId() == null || request.getBarcode() == null) {
            response.put("error", "잘못된 요청 데이터");
            return ResponseEntity.badRequest().body(response);
        }

        ShoppingCart savedItem = shoppingCartService.addToCart(request.getUserId(), request.getBarcode());

        if (savedItem.getCartItemNo() != null) {
            response.put("message", "장바구니에 추가되었습니다.");
            return ResponseEntity.ok(response);  // 200 OK 응답 반환
        } else {
            response.put("error", "장바구니 추가 실패");
            return ResponseEntity.badRequest().body(response); // 400 응답 반환 방지
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Barcode>> getCartItems(@PathVariable("userId") String userId) {
        List<Barcode> cartItems = shoppingCartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
    
    @GetMapping("/{userId}/totalPrice")
    public ResponseEntity<Map<String, Integer>> getTotalCartPrice(@PathVariable("userId") String userId) {
        int totalPrice = shoppingCartService.calculateTotalPrice(userId);
        Map<String, Integer> response = new HashMap<>();
        response.put("totalPrice", totalPrice);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteCartItem(
            @RequestParam("userId") String userId,
            @RequestParam("barcode") String barcode) {

        Map<String, String> response = new HashMap<>();

        boolean deleted = shoppingCartService.deleteCartItem(userId, barcode);
        if (deleted) {
            response.put("message", "삭제 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "삭제 실패");
            return ResponseEntity.badRequest().body(response);
        }
    }

}

// 요청을 받기 위한 DTO
class CartRequest {
    private String userId;
    private String barcode;

    // Getter / Setter
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

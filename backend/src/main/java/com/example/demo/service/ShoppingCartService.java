package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Barcode;
import com.example.demo.model.ShoppingCart;
import com.example.demo.repository.BarcodeRepository;
import com.example.demo.repository.ShoppingCartRepository;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    
    @Autowired
    private BarcodeRepository barcodeRepository;
    
    /**
     * 장바구니에 상품 추가
     * @param userId  로그인한 사용자 ID
     * @param barcode 스캔한 바코드
     * @return 저장된 ShoppingCart 객체
     */
    public ShoppingCart addToCart(String userId, String barcode) {
        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setUserId(userId);     // user_id 컬럼
        cartItem.setBarcode(barcode);   // barcode 컬럼
        return shoppingCartRepository.save(cartItem);
    }
    
    public List<Barcode> getCartItems(String userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        List<Barcode> barcodeList = new ArrayList<>();

        for (ShoppingCart item : cartItems) {
            barcodeRepository.findById(item.getBarcode()).ifPresent(barcodeList::add);
        }
        return barcodeList;
    }
    
    public boolean deleteCartItem(String userId, String barcode) {
        List<ShoppingCart> items = shoppingCartRepository.findByUserId(userId);
        for (ShoppingCart item : items) {
            if (item.getBarcode().equals(barcode)) {
                shoppingCartRepository.delete(item);
                return true;
            }
        }
        return false;
    }

    
    public int calculateTotalPrice(String userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        int totalPrice = 0;

        for (ShoppingCart item : cartItems) {
            Optional<Barcode> barcode = barcodeRepository.findById(item.getBarcode());
            if (barcode.isPresent()) {
                totalPrice += barcode.get().getPrice();
            }
        }
        return totalPrice;
    }

}

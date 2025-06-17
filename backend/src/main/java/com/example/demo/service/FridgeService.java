package com.example.demo.service;

import com.example.demo.model.Fridge;
import com.example.demo.repository.FridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    public List<Fridge> getFridgeItems(String userId) {
        return fridgeRepository.findByUserId(userId);
    }
    
    
    
    public Optional<String> getPhotoAsBase64(String userId, String ingredientName) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findByUserIdAndFridgeIngredient(userId, ingredientName);

        if (fridgeOpt.isPresent()) {
            byte[] photoBytes = fridgeOpt.get().getPhoto();
            if (photoBytes != null && photoBytes.length > 0) {
                return Optional.of(Base64.getEncoder().encodeToString(photoBytes));
            }
        }
        return Optional.empty(); // null이거나 없으면 빈 Optional 반환
    }

}

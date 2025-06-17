package com.example.demo.controller;

import com.example.demo.model.Fridge;
import com.example.demo.model.FridgeDTO;
import com.example.demo.service.FridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fridge")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FridgeDTO>> getFridgeItems(@PathVariable("userId") String userId) {
        List<Fridge> items = fridgeService.getFridgeItems(userId);

        List<FridgeDTO> result = items.stream().map(fridge -> {
            FridgeDTO dto = new FridgeDTO();
            dto.setFridgeIngredientNo(fridge.getFridgeIngredientNo());
            dto.setUserId(fridge.getUserId());
            dto.setFridgeIngredient(fridge.getFridgeIngredient());
            return dto;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/photo")
    public ResponseEntity<String> getFridgePhoto(
        @RequestParam("userId") String userId,
        @RequestParam("ingredientName") String ingredientName
    ) {
        // Optional<String>이 비어 있어도 200 OK로 응답, body는 ""로
        String base64 = fridgeService.getPhotoAsBase64(userId, ingredientName).orElse("");
        return ResponseEntity.ok(base64);
    }


}

package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.PreferIngredient;
import com.example.demo.repository.PreferIngredientRepository;

@RestController
@RequestMapping("/api/prefer-ingredient")
public class PreferIngredientController {

    @Autowired
    private PreferIngredientRepository preferIngredientRepository;

    @GetMapping("/exists")
    public ResponseEntity<Boolean> isFavorite(
        @RequestParam("userId") String userId,
        @RequestParam("ingredient") String ingredient) {

    	 boolean exists = preferIngredientRepository.findByUserId(userId).stream()
    		        .anyMatch(i ->
    		            i.getPreferIngredient().trim().equalsIgnoreCase(ingredient.trim())
    		        );
    	 return ResponseEntity.ok(exists);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody PreferIngredient newFavorite) {
    	 System.out.println("userId: " + newFavorite.getUserId());
    	 System.out.println("preferIngredient: " + newFavorite.getPreferIngredient());

    	try {
            preferIngredientRepository.save(newFavorite);
            return ResponseEntity.ok().body(Map.of("message", "즐겨찾기에 추가되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "추가 실패"));
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFavorite(
            @RequestParam("userId") String userId,
            @RequestParam("ingredient") String ingredient) {
        try {
            List<PreferIngredient> matches = preferIngredientRepository.findByUserId(userId).stream()
                .filter(i -> i.getPreferIngredient().trim().equalsIgnoreCase(ingredient.trim()))
                .toList();

            preferIngredientRepository.deleteAll(matches);
            return ResponseEntity.ok().body(Map.of("message", "즐겨찾기에서 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "삭제 실패"));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<PreferIngredient>> getUserFavorites(@RequestParam("userId") String userId) {
        List<PreferIngredient> list = preferIngredientRepository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    
}

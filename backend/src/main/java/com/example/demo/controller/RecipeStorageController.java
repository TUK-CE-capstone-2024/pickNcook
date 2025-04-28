package com.example.demo.controller;

import com.example.demo.model.RecipeStorage;
import com.example.demo.service.RecipeStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recipe-storage")
public class RecipeStorageController {

    @Autowired
    private RecipeStorageService recipeStorageService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> saveRecipe(@RequestBody RecipeStorage recipeStorage) {
        recipeStorageService.saveRecipe(recipeStorage);
        Map<String, String> response = new HashMap<>();
        response.put("message", "레시피 저장 성공");
        return ResponseEntity.ok(response);
    }
}

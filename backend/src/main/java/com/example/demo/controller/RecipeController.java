package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Recipe;
import com.example.demo.model.RecipeDetailDTO;
import com.example.demo.model.RecipeFilterRequest;
import com.example.demo.service.RecipeService;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/detail/{recipeNo}")
    public ResponseEntity<Recipe> getRecipeDetail(@PathVariable("recipeNo") int recipeNo) {
        Recipe recipe = recipeService.getRecipeByNo(recipeNo);
        if (recipe == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(recipe); // DTO 없이 직접 반환
    }
    
    // 이건 DTO 방식
    @GetMapping("/info/{recipeNo}")
    public ResponseEntity<?> getRecipeInfo(@PathVariable("recipeNo") int recipeNo) {
        RecipeDetailDTO dto = recipeService.getRecipeDetailDTO(recipeNo);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    
    
    
    // 1. 전체 레시피
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // 2. 레시피 추천을 위한 필터
    @PostMapping("/filter")
    public List<Recipe> filterRecipes(@RequestBody RecipeFilterRequest request) {
        return recipeService.filterRecipes(
            request.getKind(),
            request.getSituation(),
            request.getMethod(),
            request.getIngredients()
        );
    }





    // 3. 카테고리 값 조회
    @GetMapping("/categories/{column}")
    public List<String> getCategoryValues(@PathVariable String column) {
        List<Recipe> all = recipeService.getAllRecipes();
        return recipeService.getCategoryValues(column, all);
    }

    // 4. 이름으로 이미지 URL 조회
    @GetMapping("/image-url")
    public String getImageUrl(@RequestParam String name) {
        return recipeService.getImageUrlByName(name);
    }

    // 5. 레시피 번호로 조회
    @GetMapping("/{recipeNo}")
    public Recipe getRecipeByNo(@PathVariable("recipeNo") Integer recipeNo) {
        return recipeService.getRecipeByNo(recipeNo);
    }
}

package com.example.demo.controller;

import com.example.demo.model.Recipe;
import com.example.demo.model.RecipeStorage;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.RecipeStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe-storage")
public class SavedRecipeController {

    @Autowired
    private RecipeStorageRepository recipeStorageRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/{userId}")
    public List<String> getSavedRecipes(@PathVariable("userId") String userId) {
        List<RecipeStorage> savedList = recipeStorageRepository.findByUserId(userId);

        return savedList.stream()
                .map(storage -> {
                    Recipe recipe = recipeRepository.findById(storage.getRecipeNo()).orElse(null);
                    return recipe != null ? recipe.getCkgNm() : null;
                })
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }
}

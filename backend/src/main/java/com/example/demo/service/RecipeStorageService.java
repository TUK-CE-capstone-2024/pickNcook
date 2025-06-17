package com.example.demo.service;

import com.example.demo.model.RecipeStorage;
import com.example.demo.repository.RecipeStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeStorageService {

    @Autowired
    private RecipeStorageRepository recipeStorageRepository;

    public RecipeStorage saveRecipe(RecipeStorage recipeStorage) {
        return recipeStorageRepository.save(recipeStorage);
    }
    
    public boolean isRecipeSaved(String userId, int recipeNo) {
        return recipeStorageRepository.findByUserId(userId).stream()
            .anyMatch(rs -> rs.getRecipeNo() == recipeNo);
    }
    
    @Transactional
    public void deleteRecipe(String userId, int recipeNo) {
        recipeStorageRepository.deleteByUserIdAndRecipeNo(userId, recipeNo);
    }

}

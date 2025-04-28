package com.example.demo.service;

import com.example.demo.model.RecipeStorage;
import com.example.demo.repository.RecipeStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeStorageService {

    @Autowired
    private RecipeStorageRepository recipeStorageRepository;

    public RecipeStorage saveRecipe(RecipeStorage recipeStorage) {
        return recipeStorageRepository.save(recipeStorage);
    }
}

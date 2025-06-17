package com.example.demo.repository;

import com.example.demo.model.RecipeStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeStorageRepository extends JpaRepository<RecipeStorage, Integer> {
    List<RecipeStorage> findByUserId(String userId); 
    void deleteByUserIdAndRecipeNo(String userId, int recipeNo);
}

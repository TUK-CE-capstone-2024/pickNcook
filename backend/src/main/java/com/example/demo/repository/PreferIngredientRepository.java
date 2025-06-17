package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.PreferIngredient;

@Repository
public interface PreferIngredientRepository extends JpaRepository<PreferIngredient, Integer> {
    boolean existsByUserIdAndPreferIngredient(String userId, String preferIngredient);

    List<PreferIngredient> findByUserId(String userId);
}

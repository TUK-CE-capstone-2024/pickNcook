package com.example.demo.repository;

import com.example.demo.model.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Integer> {
    List<Fridge> findByUserId(String userId);
    
    Optional<Fridge> findByUserIdAndFridgeIngredient(String userId, String fridgeIngredient);
}

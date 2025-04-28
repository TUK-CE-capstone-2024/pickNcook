package com.example.demo.repository;

import com.example.demo.model.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Integer> {
    List<Fridge> findByUserId(String userId);
}

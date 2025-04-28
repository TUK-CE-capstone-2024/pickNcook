package com.example.demo.service;

import com.example.demo.model.Fridge;
import com.example.demo.repository.FridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    public List<Fridge> getFridgeItems(String userId) {
        return fridgeRepository.findByUserId(userId);
    }
}

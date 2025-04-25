package com.example.demo.service;

import com.example.demo.model.ShoppingListDetail;
import com.example.demo.repository.ShoppingListDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShoppingListDetailService {

    @Autowired
    private ShoppingListDetailRepository repository;

    public List<ShoppingListDetail> getDetailsByListNo(int listNo) {
        return repository.findByListNo(listNo);
    }
    
    public void deleteIngredientById(int detailNo) {
        repository.deleteById(detailNo);
    }

    public void addIngredient(int listNo, String ingredientName) {
        ShoppingListDetail detail = new ShoppingListDetail();
        detail.setListNo(listNo);
        detail.setIngredient(ingredientName);
        repository.save(detail);
    }

}

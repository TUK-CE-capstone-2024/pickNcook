package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.ShoppingList;
import com.example.demo.repository.ShoppingListRepository;

@Service
public class ShoppingListService {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    public List<ShoppingList> getShoppingListsByUserId(String userId) {
        return shoppingListRepository.findByUserId(userId);
    }

    public void addShoppingList(String userId, String listName) {
        ShoppingList list = new ShoppingList();
        list.setUserId(userId);
        list.setListName(listName); // 예: "쇼핑리스트" 또는 "새 쇼핑리스트"
        shoppingListRepository.save(list);
    }

    //쇼핑리스트 제목 바꾸기
    public void updateListName(int listId, String newName) {
        Optional<ShoppingList> optional = shoppingListRepository.findById(listId);
        optional.ifPresent(list -> {
            list.setListName(newName);
            shoppingListRepository.save(list);
        });
    }
    
    public void deleteList(int listId) {
        shoppingListRepository.deleteById(listId);
    }

    
    
}

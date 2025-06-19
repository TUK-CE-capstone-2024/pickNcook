package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ShoppingList;
import com.example.demo.model.ShoppingListDetail;
import com.example.demo.service.ShoppingListDetailService;
import com.example.demo.service.ShoppingListService;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private ShoppingListDetailService detailService;
    
    // 로그인한 사용자의 쇼핑리스트 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<ShoppingList>> getShoppingLists(@PathVariable("userId") String userId) {
        List<ShoppingList> lists = shoppingListService.getShoppingListsByUserId(userId);
        return ResponseEntity.ok(lists);
    }

    // 새로운 쇼핑리스트 생성
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addShoppingList(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String listName = request.get("listName");

        shoppingListService.addShoppingList(userId, listName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "쇼핑리스트가 추가되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 쇼핑리스트 제목 수정
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateListName(@RequestBody Map<String, String> request) {
        int listId = Integer.parseInt(request.get("listId"));
        String newName = request.get("newName");

        shoppingListService.updateListName(listId, newName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "제목이 수정되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    
    
    @DeleteMapping("/delete/{listId}")
    public ResponseEntity<Map<String, String>> deleteShoppingList(@PathVariable("listId") int listId) {
        shoppingListService.deleteList(listId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "리스트가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    
    
    
    @GetMapping("/detail/{listNo}")
    public ResponseEntity<List<ShoppingListDetail>> getListDetails(@PathVariable("listNo") int listNo) {
        List<ShoppingListDetail> details = detailService.getDetailsByListNo(listNo);
        return ResponseEntity.ok(details);
    }
    
    @DeleteMapping("/detail/delete/{detailNo}")
    public ResponseEntity<Map<String, String>> deleteIngredient(@PathVariable("detailNo") int detailNo) {
        detailService.deleteIngredientById(detailNo); // 서비스 계층 호출

        Map<String, String> response = new HashMap<>();
        response.put("message", "재료가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/detail/add")
    public ResponseEntity<Map<String, String>> addIngredient(
            @RequestParam("listNo") int listNo,
            @RequestParam("ingredientName") String ingredientName) {

        detailService.addIngredient(listNo, ingredientName); //서비스 호출

        Map<String, String> response = new HashMap<>();
        response.put("message", "재료가 추가되었습니다.");
        return ResponseEntity.ok(response);
    }

    




}

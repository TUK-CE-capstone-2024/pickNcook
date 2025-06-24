package com.example.demo.controller;

import com.example.demo.model.Fridge;
import com.example.demo.model.Notification;
import com.example.demo.model.PreferIngredient;
import com.example.demo.model.RemovedFavoriteCheckRequest;
import com.example.demo.repository.FridgeRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.PreferIngredientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private PreferIngredientRepository preferIngredientRepository;
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable("userId") String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByRegDateDesc(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @DeleteMapping("/{notificationNo}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationNo") int notificationNo) {
        notificationRepository.deleteById(notificationNo);
        return ResponseEntity.noContent().build();
    }
    
    
    
    @PostMapping("/check-removed-favorites")
    public ResponseEntity<?> checkRemovedFavorites(@RequestBody RemovedFavoriteCheckRequest request) {
        String userId = request.getUserId();
        List<String> oldItems = request.getPreviousFridgeItems();

        List<String> currentFridgeItems = fridgeRepository.findByUserId(userId)
            .stream()
            .map(Fridge::getFridgeIngredient)
            .toList();
        
        System.out.println("현재 식재료: " + currentFridgeItems);
        
        List<String> favoriteItems = preferIngredientRepository.findByUserId(userId)
            .stream()
            .map(PreferIngredient::getPreferIngredient)
            .toList();

        List<String> removedFavorites = favoriteItems.stream()
            .filter(fav -> oldItems.contains(fav) && !currentFridgeItems.contains(fav))
            .toList();

        for (String ingredient : removedFavorites) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setNotificationMsg("선호 식재료인 '" + ingredient + "' 이(가) 냉장고에 없습니다.");
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok(Map.of("message", "알림 저장 완료"));
    }


}

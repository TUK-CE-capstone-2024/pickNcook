package com.example.demo.repository;

import com.example.demo.model.ShoppingListDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShoppingListDetailRepository extends JpaRepository<ShoppingListDetail, Integer> {
    List<ShoppingListDetail> findByListNo(int listNo);
}

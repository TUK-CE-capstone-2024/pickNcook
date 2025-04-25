package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    // 아이디 중복 체크용 메서드
    boolean existsById(String id);
}
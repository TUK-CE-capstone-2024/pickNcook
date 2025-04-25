package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/main")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // POST /api/register 로 회원가입 요청 처리
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
    	Map<String, String> response = new HashMap<>();
    	
    	// 아이디 중복 체크
        if(userRepository.existsById(user.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이미 존재하는 아이디입니다.");
        }

        // 실제 운영 환경에서는 비밀번호 암호화(Bcrypt 등) 처리를 권장합니다.
        userRepository.save(user);
        response.put("message", "회원가입 성공");
        return ResponseEntity.ok(response);
    }
    
    
    
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String id = loginRequest.get("id");
        String password = loginRequest.get("password");

        // 아이디로 사용자 검색
        Optional<User> optionalUser = userRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (!optionalUser.isPresent()) {
            response.put("error", "존재하지 않는 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = optionalUser.get();
        // 실제 운영 환경에서는 반드시 비밀번호 암호화 비교를 해야 합니다.
        if (!user.getPassword().equals(password)) {
            response.put("error", "비밀번호가 틀렸습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "로그인 성공");
        response.put("userId", user.getId());  // 사용자 아이디 반환
        response.put("userName", user.getUserName());
        return ResponseEntity.ok(response);
    }
}

package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.User;
import com.shelfsmart.shelfsmart_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.shelfsmart.shelfsmart_backend.service.UserActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityService userActivityService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        userActivityService.logActivity(registeredUser, "REGISTER", "User registered with email: " + user.getEmail());
        return ResponseEntity.status(201).body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String token = userService.loginUser(credentials.get("email"), credentials.get("password"));
        User user = userService.getUserByEmail(credentials.get("email"))
                .orElseThrow(() -> new RuntimeException("User not found")); // Fetch user for logging
        userActivityService.logActivity(user, "LOGIN", "User logged in");
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        User user = userService.getCurrentUser(); // Get current user before logout
        userActivityService.logActivity(user, "LOGOUT", "User logged out");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        user.setPassword(null); // Hide sensitive field
        return ResponseEntity.ok(user);
    }
}
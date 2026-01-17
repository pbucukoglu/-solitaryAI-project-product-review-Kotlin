package com.productreview.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String hashedAdmin = encoder.encode(adminPassword);
        String hashedUser = encoder.encode(userPassword);
        
        System.out.println("=== HASHED PASSWORDS FOR RAILWAY ===");
        System.out.println("Admin password hash: " + hashedAdmin);
        System.out.println("User password hash: " + hashedUser);
        System.out.println("=====================================");
    }
}

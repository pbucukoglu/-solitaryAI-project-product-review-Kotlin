package com.productreview.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class PasswordGenerator {
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public void run(String... args) throws Exception {
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String hashedAdmin = passwordEncoder.encode(adminPassword);
        String hashedUser = passwordEncoder.encode(userPassword);
        
        System.out.println("=== HASHED PASSWORDS FOR RAILWAY ===");
        System.out.println("Admin password hash: " + hashedAdmin);
        System.out.println("User password hash: " + hashedUser);
        System.out.println("=====================================");
    }
}

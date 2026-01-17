package com.productreview.config;

import com.productreview.entity.User;
import com.productreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }
    
    private void initializeUsers() {
        log.info("Initializing default users...");
        
        User adminUser = new User();
        adminUser.setEmail("admin@productreview.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRoles(List.of("ADMIN"));
        adminUser.setIsActive(true);
        
        User normalUser = new User();
        normalUser.setEmail("user@productreview.com");
        normalUser.setPassword(passwordEncoder.encode("user123"));
        normalUser.setFirstName("Regular");
        normalUser.setLastName("User");
        normalUser.setRoles(List.of("USER"));
        normalUser.setIsActive(true);
        
        userRepository.save(adminUser);
        userRepository.save(normalUser);
        
        log.info("Created default users:");
        log.info("Admin: admin@productreview.com / admin123");
        log.info("User: user@productreview.com / user123");
    }
}

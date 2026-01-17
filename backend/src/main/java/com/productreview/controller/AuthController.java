package com.productreview.controller;

import com.productreview.dto.LoginRequestDTO;
import com.productreview.dto.LoginResponseDTO;
import com.productreview.dto.RegisterRequestDTO;
import com.productreview.entity.User;
import com.productreview.repository.UserRepository;
import com.productreview.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email is already registered");
        }
        
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .role("User")
                .enabled(true)
                .build();
        
        userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user, user.getId());
        
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600)
                .roles(roles)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

package com.productreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private List<String> roles;
    private String email;
    private String firstName;
    private String lastName;
}

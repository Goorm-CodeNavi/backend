package com.codenavi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class JwtResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}

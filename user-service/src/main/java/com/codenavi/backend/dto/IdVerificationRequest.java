package com.codenavi.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdVerificationRequest {
    private String email;
    private String code;
}
package com.codenavi.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 수정을 위한 요청 데이터를 담는 DTO 입니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
//    private String phoneNumber;
}

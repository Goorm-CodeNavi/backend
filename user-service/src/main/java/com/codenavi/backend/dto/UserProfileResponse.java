package com.codenavi.backend.dto;

import com.codenavi.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// 사용자 정보를 담아서 클라이언트에게 보낼 DTO
@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime created_at;
    // 필요에 따라 프로필 정보, 역할 등 추가 필드를 넣을 수 있습니다.

    // User 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static UserProfileResponse fromUser(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
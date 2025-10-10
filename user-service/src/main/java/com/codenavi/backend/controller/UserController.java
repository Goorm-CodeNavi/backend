package com.codenavi.backend.controller;

import com.codenavi.backend.dto.UserProfileResponse;
import com.codenavi.backend.dto.UserUpdateRequest;
import com.codenavi.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 정보 조회 및 수정을 위한 컨트롤러 클래스입니다.
 * 모든 API는 /api/users 경로를 기본으로 합니다.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * JWT 토큰을 통해 인증된 사용자의 정보를 반환합니다.
     * @param userDetails Spring Security가 주입해주는 현재 사용자 정보
     * @return 사용자의 프로필 정보가 담긴 ResponseEntity
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 서비스 레이어를 호출하여 사용자 프로필 정보를 가져옵니다.
        UserProfileResponse userProfile = userService.getUserProfile(userDetails.getUsername());
        // 성공 응답(200 OK)과 함께 사용자 정보를 반환합니다.
        return ResponseEntity.ok(userProfile);
    }

    /**
     * 현재 로그인한 사용자의 프로필 정보를 수정합니다.
     * @param userDetails Spring Security가 주입해주는 현재 사용자 정보
     * @param updateRequest 클라이언트로부터 받은 수정할 사용자 정보
     * @return 갱신된 사용자의 프로필 정보가 담긴 ResponseEntity
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest updateRequest) {

        // 서비스 레이어를 호출하여 사용자 정보를 업데이트합니다.
        UserProfileResponse updatedProfile = userService.updateUserProfile(userDetails.getUsername(), updateRequest);
        // 성공 응답(200 OK)과 함께 갱신된 정보를 반환합니다.
        return ResponseEntity.ok(updatedProfile);
    }
}


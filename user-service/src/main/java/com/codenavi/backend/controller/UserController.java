package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.service.SolutionService;
import com.codenavi.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;
    private final SolutionService solutionService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 서비스 레이어를 호출하여 사용자 프로필 정보를 가져옵니다.
        UserProfileResponse userProfile = userService.getUserProfile(userDetails.getUsername());
        // 성공 응답(200 OK)과 함께 사용자 정보를 반환합니다.
        return ResponseEntity.ok(ApiResponse.onSuccess(userProfile));
    }

    // 내 정보 수정
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserUpdateRequest updateRequest) {

        // 서비스 레이어를 호출하여 사용자 정보를 업데이트합니다.
        UserProfileResponse updatedProfile = userService.updateUserProfile(userDetails.getUsername(), updateRequest);
        // 성공 응답(200 OK)과 함께 갱신된 정보를 반환합니다.
        return ResponseEntity.ok(ApiResponse.onSuccess(updatedProfile));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> updateCurrentUserPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {

        try {
            userService.changePassword(userDetails.getUsername(), request);
            return ResponseEntity.ok(ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            // Service에서 던진 예외를 잡아서 400 에러로 응답
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.onFailure("USER4001", e.getMessage(), null));
        }
    }

    @GetMapping("/me/submissions")
    public ResponseEntity<ApiResponse<?>> getMySubmissions(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {

        // JWT 토큰에서 현재 로그인한 사용자의 이름(username)을 가져옵니다.
        String username = authentication.getName();

        Page<SolutionHistoryDto> historyPage = solutionService.getSolutionHistoryForUser(username, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(historyPage));
    }



}


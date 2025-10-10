package com.codenavi.backend.controller;

import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.UserProfileResponse;
import com.codenavi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security 컨텍스트에서 현재 인증된 사용자의 username을 가져옵니다.
        String username = userDetails.getUsername();

        // username으로 DB에서 User 정보를 찾습니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        // User 엔티티를 UserProfileResponse DTO로 변환합니다.
        UserProfileResponse userProfileResponse = UserProfileResponse.fromUser(user);

        return ResponseEntity.ok(userProfileResponse);
    }

}
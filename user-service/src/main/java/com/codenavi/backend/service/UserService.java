package com.codenavi.backend.service;

import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.UserProfileResponse;
import com.codenavi.backend.dto.UserUpdateRequest;
import com.codenavi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 유저 프로필 정보 가져오기
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        return UserProfileResponse.fromUser(user);
    }

    // 유저 프로필 정보 업데이트
    @Transactional
    public UserProfileResponse updateUserProfile(String username, UserUpdateRequest updateRequest) {
        // 1. DB에서 현재 사용자 정보를 가져옵니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        // 2. DTO에 담겨온 정보로 사용자 엔티티의 값을 변경합니다.
        user.setEmail(updateRequest.getEmail());
        user.setUsername(updateRequest.getUsername());

        User updatedUser = userRepository.save(user);

        // 5. 갱신된 정보를 DTO로 변환하여 반환합니다.
        return UserProfileResponse.fromUser(updatedUser);
    }
}

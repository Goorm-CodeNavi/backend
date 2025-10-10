package com.codenavi.backend.service;

import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.UserProfileResponse;
import com.codenavi.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 이름을 기반으로 사용자 정보를 조회하고 DTO로 변환하여 반환합니다.
     * @param username 조회할 사용자의 이름
     * @return UserProfileResponse DTO
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        return UserProfileResponse.fromUser(user);
    }
}

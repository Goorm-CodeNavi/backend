package com.codenavi.backend.service;

import com.codenavi.backend.domain.User;
import com.codenavi.backend.dto.PasswordChangeRequest;
import com.codenavi.backend.dto.UserProfileResponse;
import com.codenavi.backend.dto.UserUpdateRequest;
import com.codenavi.backend.exception.InvalidPasswordException;
import com.codenavi.backend.repository.SolutionRepository;
import com.codenavi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SolutionRepository solutionRepository;

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

    // 임시 비밀번호 인증된 이메일로 전송
    @Transactional
    public void issueTemporaryPassword(String username, String email) {
        // 아이디와 이메일로 사용자 찾기
        User user = userRepository.findByUsernameAndEmail(username, email)
                .orElse(null);
        if (user == null) {
            return;
        }
        String temporaryPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        emailService.sendTemporaryPassword(email, temporaryPassword);
    }

    // 임의의 비밀번호 생성을 위한 private 헬퍼 메소드
    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    // 비밀번호 재설정
    @Transactional
    public void changePassword(String username, PasswordChangeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1. 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다."); // 403 Forbidden 유발
        }

        // 2. 새 비밀번호와 확인용 비밀번호가 일치하는지 확인
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다."); // 400 Bad Request 유발
        }

        // 3. 새 비밀번호를 암호화하여 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}

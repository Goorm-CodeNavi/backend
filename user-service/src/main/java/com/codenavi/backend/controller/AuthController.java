package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.jwt.JwtTokenProvider;
import com.codenavi.backend.repository.UserRepository;
import com.codenavi.backend.service.EmailService;
import com.codenavi.backend.service.VerificationService;
import com.codenavi.backend.service.UserService;
import lombok.RequiredArgsConstructor; // 생성자 주입을 위한 Lombok 어노테이션
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class AuthController {
    // 의존성을 final로 선언하여 불변성 확보
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final UserService userService;


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@RequestBody LoginRequest loginRequest) {

        if (loginRequest.getUsername() == null || loginRequest.getUsername().isBlank() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                    .badRequest() // 400 Bad Request
                    .body(ApiResponse.onFailure("COMMON400", "잘못된 요청입니다.", "아이디와 비밀번호를 모두 입력해주세요."));
        }

        try {
            // --- 인증 시도 ---
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // --- 성공 응답 ---
            return ResponseEntity.ok(ApiResponse.onSuccess(new JwtResponse(jwt)));

        } catch (AuthenticationException e) {
            // --- 상황 2: 인증 실패 (아이디 또는 비밀번호 불일치) ---
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .body(ApiResponse.onFailure("LOGIN4001", "로그인에 실패했습니다.", "아이디 또는 비밀번호를 확인해주세요."));
        }
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (signUpRequest.getPassword().length() < 8) {
            return ResponseEntity
                    .badRequest() // 400 상태 코드
                    .body(ApiResponse.onFailure("SIGNUP4000", "비밀번호는 8자 이상이어야 합니다.", "입력값을 확인해주세요."));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            // 409 Conflict 상태 코드와 ApiResponse 포맷으로 실패 응답
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("SIGNUP4091", "이미 사용 중인 아이디입니다.", null));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            // 409 Conflict 상태 코드와 ApiResponse 포맷으로 실패 응답
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("SIGNUP4092", "이미 사용 중인 이메일입니다.", null));
        }

        // Creating user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        userRepository.save(user);

        // 201 Created 상태 코드와 ApiResponse 포맷으로 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess("회원가입에 성공했습니다."));
    }

    // 아이디 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<String>> checkUsername(@RequestParam("username") String username) {
        if (username == null || username.isBlank()) {
            return ResponseEntity
                    .badRequest() // 400 Bad Request
                    .body(ApiResponse.onFailure("COMMON400", "잘못된 요청입니다.", "아이디를 입력해주세요."));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("AUTH4091", "이미 사용 중인 아이디입니다.", null));
        } else {
            return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 아이디입니다."));
        }
    }

    // 아이디 찾기 - 이메일 인증
    @PostMapping("/find-id/send-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCodeForId(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.ok(ApiResponse.onSuccess("인증번호가 발송되었습니다. 이메일 수신함을 확인해주세요."));
        }

        String code = verificationService.generateCode();
        verificationService.saveCode(email, code);
        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok(ApiResponse.onSuccess("인증번호가 성공적으로 발송되었습니다."));
    }

    // 아이디찾기-코드인증
    @PostMapping("/find-id/verify-code")
    public ResponseEntity<ApiResponse<?>> verifyCodeAndFindId(@RequestBody IdVerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();
        String code = verificationRequest.getCode();

        boolean isVerified = verificationService.verifyCode(email, code);

        if (!isVerified) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.onFailure("AUTH4001", "인증에 실패했습니다.", "인증번호가 올바르지 않거나 만료되었습니다."));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("인증 후 사용자를 찾을 수 없음"));

        return ResponseEntity.ok(ApiResponse.onSuccess(new UsernameResponse(user.getUsername())));
    }

    // 임시 비밀번호 발급
    @PostMapping("/reset-password/issue-temporary")
    public ResponseEntity<ApiResponse<String>> issueTemporaryPassword(@RequestBody PasswordResetRequest request) {
        userService.issueTemporaryPassword(request.getUsername(), request.getEmail());

        // 사용자가 존재하든 안하든 항상 동일한 성공 메시지를 보내 보안을 강화합니다.
        return ResponseEntity.ok(ApiResponse.onSuccess("입력하신 이메일로 임시 비밀번호가 발송되었습니다. 이메일 수신함을 확인해주세요."));
    }
}
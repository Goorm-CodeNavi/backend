package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.domain.User;
import com.codenavi.backend.jwt.JwtTokenProvider;
import com.codenavi.backend.repository.UserRepository;
import com.codenavi.backend.service.EmailService;
import com.codenavi.backend.service.VerificationService;
import com.codenavi.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth", description = "사용자 로그인, 회원가입, 아이디/비밀번호 찾기 등 인증 관련 기능을 제공합니다.")
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
    @Operation(summary = "로그인", description = "사용자 아이디와 비밀번호로 로그인하여 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (아이디/비밀번호 누락)", content = @Content(schema = @Schema(implementation = com.codenavi.backend.dto.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)", content = @Content(schema = @Schema(implementation = com.codenavi.backend.dto.ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> authenticateUser(@RequestBody LoginRequest loginRequest) {

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
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 비밀번호 8자 미만)", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "데이터 중복 (이미 존재하는 아이디 또는 이메일)", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (signUpRequest.getPassword().length() < 8) {
            return ResponseEntity
                    .badRequest() // 400 상태 코드
                    .body(ApiResponse.onFailure("SIGNUP4000", "잘못된 요청입니다.", "비밀번호는 8자 이상이어야 합니다."));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            // 409 Conflict 상태 코드와 ApiResponse 포맷으로 실패 응답
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("SIGNUP4091", "이미 존재하는 아이디입니다.", null));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            // 409 Conflict 상태 코드와 ApiResponse 포맷으로 실패 응답
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("SIGNUP4092", "이미 존재하는 이메입니다.", null));
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
    @Operation(summary = "아이디 중복 확인", description = "입력한 아이디가 사용 가능한지 확인합니다.")
    @Parameter(in = ParameterIn.QUERY, name = "username", description = "중복 확인할 아이디", required = true, example = "testuser123")
    @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용 가능한 아이디"),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (아이디 미입력)", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 아이디", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
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
                    .body(ApiResponse.onFailure("AUTH4091", "이미 존재하는 아이디입니다.", null));
        } else {
            return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 아이디입니다."));
        }
    }

    // 아이디 찾기 - 코드 발급
    @Operation(summary = "아이디 찾기용 인증코드 발송", description = "가입된 이메일 주소로 인증 코드를 발송합니다. 가입되지 않은 이메일이라도 보안을 위해 동일한 성공 응답을 반환합니다.")
    @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 코드 발송 요청 성공")
    })
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
    @Operation(summary = "아이디 찾기 (코드 인증)", description = "이메일로 받은 인증 코드를 확인하여 일치하면 해당 이메일로 가입된 아이디를 반환합니다.")
    @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 성공 및 아이디 반환", content = @Content(schema = @Schema(implementation = UsernameResponse.class))),
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패 (코드가 올바르지 않거나 만료됨)", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
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
    @Operation(summary = "임시 비밀번호 발급", description = "아이디와 이메일이 일치하는 사용자가 있으면 해당 이메일로 임시 비밀번호를 발송합니다. 사용자가 존재하지 않아도 보안을 위해 동일한 성공 응답을 반환합니다.")
    @ApiResponses(value = {
             @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "임시 비밀번호 발송 요청 성공")
    })
    @PostMapping("/reset-password/issue-temporary")
    public ResponseEntity<ApiResponse<String>> issueTemporaryPassword(@RequestBody PasswordResetRequest request) {
        userService.issueTemporaryPassword(request.getUsername(), request.getEmail());

        // 사용자가 존재하든 안하든 항상 동일한 성공 메시지를 보내 보안을 강화합니다.
        return ResponseEntity.ok(ApiResponse.onSuccess("입력하신 이메일로 임시 비밀번호가 발송되었습니다. 이메일 수신함을 확인해주세요."));
    }
}
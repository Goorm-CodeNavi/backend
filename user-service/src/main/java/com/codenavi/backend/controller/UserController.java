package com.codenavi.backend.controller;

import com.codenavi.backend.dto.*;
import com.codenavi.backend.exception.InvalidPasswordException;
import com.codenavi.backend.service.SolutionService;
import com.codenavi.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 정보 관련 API (내 정보, 제출 기록 등)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final SolutionService solutionService;

    // 내 정보 조회
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserProfileResponse userProfile = userService.getUserProfile(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.onSuccess(userProfile));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("USER4004", "사용자를 찾을 수 없습니다.", e.getMessage()));
        }
    }

    // 내 정보 수정
    @Operation(
            summary = "내 정보 수정",
            description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 아이디 중복)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 또는 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<?>> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest updateRequest) {

        try {
            userService.updateUserProfile(userDetails.getUsername(), updateRequest);
            return ResponseEntity.ok(ApiResponse.onSuccess("사용자 정보가 성공적으로 수정되었습니다."));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("USER4004", "사용자를 찾을 수 없습니다.", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.onFailure("COMMON400", "잘못된 요청입니다.", e.getMessage()));

        } catch (Exception e) {
            // 예외 누락 대비
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("COMMON500", "서버 내부 오류가 발생했습니다.", e.getMessage()));
        }
    }

    // 비밀번호 재설정
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 현재 비밀번호 불일치)")
    })
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> updateCurrentUserPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {

        try {
            userService.changePassword(userDetails.getUsername(), request);
            return ResponseEntity.ok(ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다."));

        } catch (IllegalArgumentException e) {
            // Service에서 새 비밀번호 불일치 등으로 던진 예외 처리
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("COMMON400", "잘못된 요청입니다.", e.getMessage()));

        } catch (InvalidPasswordException e) {
            // Service에서 현재 비밀번호 불일치로 던진 예외 처리
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("AUTH4003", "권한이 없습니다.", e.getMessage()));
        }
    }

    // 제출 기록 조회
    @Operation(summary = "내 제출 기록 조회", description = "현재 로그인한 사용자의 모든 제출 기록을 페이지네이션하여 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/me/submissions")
    public ResponseEntity<ApiResponse<?>> getMySubmissions(
            Authentication authentication,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 데이터 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {

        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<SolutionHistoryDto> historyPage = solutionService.getSolutionHistoryForUser(username, pageable);
            return ResponseEntity.ok(ApiResponse.onSuccess(historyPage));
        } catch (Exception e) {
            // 예상치 못한 서버 오류 처리
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.onFailure("COMMON500", "제출 기록을 조회하는 중 오류가 발생했습니다.", null));
        }
    }
}


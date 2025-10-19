package com.codenavi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeRequest {

    @Schema(description = "현재 사용 중인 비밀번호", example = "password123")
    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
    private String currentPassword;

    @Schema(description = "새롭게 설정할 비밀번호", example = "newPassword456!")
    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "newPassword456!")
    @NotBlank(message = "새 비밀번호 확인은 필수 입력값입니다.")
    private String confirmNewPassword;
}
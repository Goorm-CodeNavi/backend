package com.codenavi.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증번호 발송 메소드
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[CodeNavi] 아이디 찾기 인증번호 안내");
        message.setText("인증번호는 [" + code + "] 입니다.");
        mailSender.send(message);
    }

    // 임시 비밀번호 발송 메소드
    public void sendTemporaryPassword(String toEmail, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[CodeNavi] 임시 비밀번호 안내");
        message.setText("로그인을 위한 임시 비밀번호입니다. 로그인 후 반드시 비밀번호를 변경해주세요.\n" +
                "임시 비밀번호: [" + temporaryPassword + "]");
        mailSender.send(message);
    }
}
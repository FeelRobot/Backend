package com.feelrobot.feelrobot.service.sign;

import com.feelrobot.feelrobot.dto.sign.*;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import org.springframework.mail.SimpleMailMessage;

public interface SignService {
    void register(RegisterDto registerDto) throws RegisterDuplicationException, ResponseException;

    LoginResponseDto login(LoginRequestDto loginRequestDto) throws ResponseException;

    void logout(String refreshToken) throws ResponseException;

    RefreshTokenResponseDto refreshToken(RefreshDto refreshDto) throws ResponseException;

    LoginResponseDto kakaoGetToken(String code) throws ResponseException;

    void checkId(String id) throws ResponseException;

    SimpleMailMessage createMail(String email, int number) throws ResponseException;

    int sendMail(MailDto mail) throws ResponseException;

    boolean checkEmail(String email, int number) throws ResponseException;

    String findId(String email) throws ResponseException;

    boolean isCorrectId(String id, String email) throws ResponseException;

    boolean existId(String email) throws ResponseException;

    void updatePassword(String id, String password) throws ResponseException;
}

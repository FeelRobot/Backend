package com.feelrobot.feelrobot.service.sign;

import com.feelrobot.feelrobot.dto.sign.LoginRequestDto;
import com.feelrobot.feelrobot.dto.sign.LoginResponseDto;
import com.feelrobot.feelrobot.dto.sign.RefreshDto;
import com.feelrobot.feelrobot.dto.sign.RegisterDto;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;

public interface SignService {
    void register(RegisterDto registerDto) throws RegisterDuplicationException, ResponseException;

    LoginResponseDto login(LoginRequestDto loginRequestDto) throws ResponseException;

    void logout(String refreshToken) throws ResponseException;

    String refreshToken(RefreshDto refreshDto) throws ResponseException;
}

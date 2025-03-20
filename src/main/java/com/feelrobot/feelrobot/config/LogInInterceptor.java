package com.feelrobot.feelrobot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
@Slf4j
public class LogInInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    //
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("[LogInInterceptor] 인터셉터 실행");
        String token = jwtTokenProvider.resolveToken(request);

        if(StringUtils.equals(request.getMethod(), "OPTIONS")){
            log.info("[LogInInterceptor] OPTIONS 요청");
            return true;
        }

        if(StringUtils.equals(request.getRequestURI(), "/aws")) {
            log.info("[LogInInterceptor] aws 요청");
            return true;
        }

        if(isNotValidatedToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }

    private boolean isNotValidatedToken(String token) {
        return token == null || !jwtTokenProvider.validationToken(token);
    }
}

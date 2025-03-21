package com.feelrobot.feelrobot.service.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feelrobot.feelrobot.config.JwtTokenProvider;
import com.feelrobot.feelrobot.dto.sign.*;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Refresh;
import com.feelrobot.feelrobot.model.User;
import com.feelrobot.feelrobot.repository.RefreshRepository;
import com.feelrobot.feelrobot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;

    @Value("${kakao.login.key}")
    private String clientId;

    @Value("${kakao.login.redirect.uri}")
    private String redirectUri;

    @Override
    public void register(RegisterDto registerDto) throws RegisterDuplicationException, ResponseException {
        log.info("[SignServiceImpl] 회원가입 요청");

        boolean isExistUser = userRepository.existsById(registerDto.getId());
        boolean isExistEmail = userRepository.existsByEmail(registerDto.getEmail());

        if(isExistUser){
            throw new RegisterDuplicationException("이미 존재하는 아이디입니다.", 400);
        }
        if(isExistEmail){
            throw new RegisterDuplicationException("이미 존재하는 이메일입니다.", 400);
        }
        try {
            User user = User.builder()
                    .id(registerDto.getId())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .name(registerDto.getName())
                    .role(registerDto.getRole())
                    .build();
            userRepository.save(user);
        } catch (Exception e) {
            log.error("[SignServiceImpl] 회원가입 실패");
            throw new ResponseException("회원가입에 실패했습니다.", 500);
        }
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws ResponseException {
        log.info("[SignServiceImpl] 로그인 요청");

        User user = userRepository.findById(loginRequestDto.getId())
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        Refresh refresh = Refresh.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build();
        refreshRepository.save(refresh);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken) throws ResponseException {
        log.info("[SignServiceImpl] 로그아웃 요청");

        if(!jwtTokenProvider.validationToken(refreshToken)){
            throw new ResponseException("유효하지 않은 토큰입니다.", 400);
        }
        Refresh refresh = refreshRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseException("존재하지 않는 토큰입니다.", 400));
        refreshRepository.deleteByToken(refreshToken);
    }

    @Override
    public String refreshToken(RefreshDto refreshDto) throws ResponseException {
        log.info("[SignServiceImpl] 토큰 재발급 요청");

        Refresh refresh = refreshRepository.findByToken(refreshDto.getRefreshToken())
                .orElseThrow(() -> new ResponseException("존재하지 않는 토큰입니다.", 400));

        if(!jwtTokenProvider.validationToken(refreshDto.getRefreshToken())){
            throw new ResponseException("유효하지 않은 토큰입니다.", 400);
        }

        return jwtTokenProvider.createAccessToken(refresh.getUserId());
    }

    @Override
    public void kakaoLogin() throws ResponseException {
        log.info("[SignServiceImpl] 카카오 로그인 요청");

        String url = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("[SignServiceImpl] 카카오 로그인 실패");
            throw new ResponseException("카카오 로그인에 실패했습니다.", 500);
        }
    }
/////
    @Override
    public void kakaoGetToken(String code) throws ResponseException {
        log.info("[SignServiceImpl] 카카오 토큰 요청");

        String url = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&code=" + code;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            KakaoRequestDto kakaoRequestDto= KakaoRequestDto.builder()
                    .grant_type("authorization_code")
                    .client_id(clientId)
                    .redirect_uri(redirectUri)
                    .code(code)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(kakaoRequestDto, Map.class);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
            RestTemplate restTemplate = new RestTemplate();
            KakaoResponseDto kakaoResponseDto = restTemplate.postForObject(url, request, KakaoResponseDto.class);
            log.info(kakaoResponseDto.getId_token());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseException("카카오 토큰 요청에 실패했습니다.", 500);
        }
    }
}

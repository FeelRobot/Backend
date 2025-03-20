package com.feelrobot.feelrobot.config;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
    public static final String TOKEN_TYPE = "tokenType";
    public static final String TOKEN_HEADER = "Auth-Token";


    @Value("${jwt.key}")
    private String tokenSecretKey;


    //한시간
    private final long jwtAccessExpiration = 1000L * 60 * 60;

    //일주일
    private final long jwtRefreshExpiration = 1000L * 60 * 60 * 24 * 7;


    /**
     * secret key 초기화
     */
    @PostConstruct
    public void init() {
        log.info("[init] secret key 초기화");
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[init] secret key 초기화 완료");
    }

    /**
     * JWT accessToken 생성
     */
    public String createAccessToken(String userId) {
        log.info("[createAccessToken] 액세스 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtAccessExpiration))
                .claim(TOKEN_TYPE, ACCESS)
                .signWith(SignatureAlgorithm.HS256, tokenSecretKey)
                .compact();

        log.info("[createAccessToken] 액세스 토큰 생성 완료");

        return accessToken;
    }

    /**
     * JWT refreshToken 생성
     */
    public String createRefreshToken() {
        log.info("[createRefreshToken] 리프레쉬 토큰 생성 시작");
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtRefreshExpiration))
                .claim(TOKEN_TYPE, REFRESH)
                .signWith(SignatureAlgorithm.HS256, tokenSecretKey)
                .compact();

        log.info("[createRefreshToken] 리프레쉬 토큰 생성 완료");

        return refreshToken;
    }

    /**
     * 토큰 기반 회원 정보 추출
     */
    public String getUserId(String accessToken) {
        log.info("[getUserId] 토큰 기반 회원 정보 추출 {}");
        Claims info = Jwts.parser().setSigningKey(tokenSecretKey).parseClaimsJws(accessToken).getBody();
        log.info("[getUserId] info = {}, userId = {}", info, info.getSubject());
        return info.getSubject();
    }


    /**
     * request 헤더에서 token 추출
     *
     * @param request
     * @return 토큰 값
     */
    public String resolveToken(HttpServletRequest request) {
        log.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader(TOKEN_HEADER);
    }

    /**
     * access token 토큰 유효 체크
     */
    public boolean validationToken(String token) {
        log.info("[validationAccessToken] 토큰 유효 체크 시작");

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(tokenSecretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("[validationAccessToken] 시간 만료 토큰");
            return false;
        } catch (Exception e) {
            log.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}
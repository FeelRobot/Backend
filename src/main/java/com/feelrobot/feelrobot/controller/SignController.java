package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.sign.LoginRequestDto;
import com.feelrobot.feelrobot.dto.sign.RefreshDto;
import com.feelrobot.feelrobot.dto.sign.RegisterDto;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.sign.SignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sign")
@Tag(name = "Sign", description = "로그인, 회원가입 API")
public class SignController {

    private final SignService signService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDto registerDto) {
        log.info("[SignController] 회원가입 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.register(registerDto);
            return new ResponseEntity<>("회원가입 성공", headers, HttpStatus.OK);
        } catch (RegisterDuplicationException e) {
            log.error("[SignController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        } catch (ResponseException e) {
            log.error("[SignController] server error");
            return new ResponseEntity<>(e.getMessage(), headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] register error");
            return new ResponseEntity<>("회원가입 실패", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        log.info("[SignController] 로그인 요청");
        try{
            return new ResponseEntity<>(signService.login(loginRequestDto), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[SignController] {}", e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 로그인 실패");
            return new ResponseEntity<>("login error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody String refreshToken) {
        log.info("[SignController] 로그아웃 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.logout(refreshToken);
            return new ResponseEntity<>("로그아웃 성공", headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] logout error");
            return new ResponseEntity<>("로그아웃 실패", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshDto refreshDto) {
        log.info("[SignController] 토큰 재발급 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            return new ResponseEntity<>(signService.refreshToken(refreshDto), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 토큰 재발급 실패");
            return new ResponseEntity<>("refresh token error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/kakao")
    public ResponseEntity<Object> kakaoLogin() {
        log.info("[SignController] 카카오 로그인 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.kakaoLogin();
            return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 카카오 로그인 실패");
            return new ResponseEntity<>("kakao login error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Object> kakaoLoginCallback(@RequestParam("code") String code) {
        log.info("[SignController] 카카오 로그인 콜백 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.kakaoGetToken(code);
            return new ResponseEntity<>("kakao login callback success", headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 카카오 로그인 콜백 실패");
            return new ResponseEntity<>("kakao login callback error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

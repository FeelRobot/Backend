package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.sign.LoginRequestDto;
import com.feelrobot.feelrobot.dto.sign.RegisterDto;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.sign.SignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try{
            signService.register(registerDto);
            return ResponseEntity.status(HttpStatus.OK).body("회원가입에 성공했습니다.");
        } catch (RegisterDuplicationException e) {
            log.error("[SignController] {}", e.getMessage());
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (ResponseException e) {
            log.error("[SignController] 서버오류");
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 회원가입 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입에 실패했습니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("[SignController] 로그인 요청");
        try{
            return ResponseEntity.status(HttpStatus.OK).body(signService.login(loginRequestDto));
        } catch (ResponseException e) {
            log.error("[SignController] 정보 불일치");
            return ResponseEntity.status(e.getResultCode()).body(e.getMessage());
        } catch (Exception e) {
            log.error("[SignController] 로그인 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인에 실패했습니다.");
        }
    }


}

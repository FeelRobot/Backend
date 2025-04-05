package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.sign.UpdateEmailDto;
import com.feelrobot.feelrobot.dto.user.CheckPasswordDto;
import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/survey")
    public ResponseEntity<Object> survey(@RequestBody SurveyResponseDto surveyResponseDto) {
        log.info("[UserController] survey");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            userService.saveSurvey(surveyResponseDto);
            return new ResponseEntity<>("설문조사 저장에 성공했습니다.",headers, HttpStatus.OK);
        } catch (ResponseException e){
            log.error("[UserController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] save survey error");
            return new ResponseEntity<>("설문조사 저장에 실패했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Object> userInfo(HttpServletRequest request) {
        log.info("[UserController] userInfo");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try {
            String userId = (String) request.getAttribute("userId");
            Object info = userService.getInfo(userId);
            return new ResponseEntity<>(info, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[UserController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] userInfo error");
            return new ResponseEntity<>("userInfo error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/password")
    public ResponseEntity<Object> checkPassword(HttpServletRequest request, @RequestBody CheckPasswordDto checkPasswordDto) {
        log.info("[UserController] checkPassword");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try {
            String userId = (String) request.getAttribute("userId");
            boolean result = userService.checkPassword(userId, checkPasswordDto.getPassword());
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[UserController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] checkPassword error");
            return new ResponseEntity<>("checkPassword error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/email")
    public ResponseEntity<Object> updateEmail(HttpServletRequest request, @RequestBody UpdateEmailDto updateEmailDto){
        log.info("[SignController] 이메일 변경 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            String userId = (String) request.getAttribute("userId");
            if(updateEmailDto.getEmail() == null || updateEmailDto.getEmail().isEmpty()){
                return new ResponseEntity<>("이메일을 입력해주세요", headers, HttpStatus.BAD_REQUEST);
            }
            userService.updateEmail(userId, updateEmailDto.getEmail());
            return new ResponseEntity<>("이메일 변경 성공", headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 이메일 변경 실패");
            return new ResponseEntity<>("update email error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

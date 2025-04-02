package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        try{
            userService.saveSurvey(surveyResponseDto);
            return new ResponseEntity<>("save survey success", HttpStatus.OK);
        } catch (ResponseException e){
            log.error("[UserController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] save survey error");
            return new ResponseEntity<>("save survey error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Object> userInfo(HttpServletRequest request) {
        log.info("[UserController] userInfo");
        try {
            String userId = (String) request.getAttribute("userId");
            Object info = userService.getInfo(userId);
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[UserController] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] userInfo error");
            return new ResponseEntity<>("userInfo error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

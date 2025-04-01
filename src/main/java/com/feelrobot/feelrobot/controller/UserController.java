package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

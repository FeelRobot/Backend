package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.ErrorDto;
import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.student.StudentService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    // 챗봇 리스트 조회
    @GetMapping("/chatbot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챗봇 리스트 조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "챗봇 리스트 조회 실패", content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "챗봇 리스트 조회 실패", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Object> getChatbotList(HttpServletRequest request) {
        String studentId = (String) request.getAttribute("userId");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        log.info("[getChatbotList] getChatbotList, input : {}", studentId);
        try {
            return new ResponseEntity<>(studentService.getChatbotList(studentId), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[getChatbotList] getChatbotList error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            log.error("[getChatbotList] getChatbotList error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("챗봇 리스트 조회에 실패했습니다.")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

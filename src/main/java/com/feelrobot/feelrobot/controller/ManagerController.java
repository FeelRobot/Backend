package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.ErrorDto;
import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.manager.ManagerService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/manager")
@Slf4j
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "학생 목록 조회 성공", content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "학생 목록 조회 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "학생 목록 조회 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> getStudentList(HttpServletRequest request) throws ResponseException {
        log.info("[ManagerController] getStudentList");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try{
            String managerId = request.getAttribute("userId").toString();
            return new ResponseEntity<>(managerService.getStudentList(managerId), HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[ManagerController] getStudentList error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[ManagerController] getStudentList error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("학생 목록 조회에 실패했습니다.")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.ErrorDto;
import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
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
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/study")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스터디 삭제 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "스터디 삭제 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "스터디 삭제 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> deleteStudy(HttpServletRequest request, @RequestParam int studyId) throws ResponseException {
        log.info("[ManagerController] deleteStudy");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try{
            String managerId = request.getAttribute("userId").toString();
            managerService.deleteStudy(managerId, studyId);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("스터디 삭제 성공")
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[ManagerController] deleteStudy error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[ManagerController] deleteStudy error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("스터디 삭제에 실패했습니다.")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/study/content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스터디 내용 조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "스터디 내용 조회 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "스터디 내용 조회 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> getStudyContent(HttpServletRequest request, @RequestParam int studyId){
        log.info("[ManagerController] getStudyContent");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try{
            String managerId = request.getAttribute("userId").toString();
            return new ResponseEntity<>(managerService.getStudyContent(managerId, studyId), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[ManagerController] getStudyContent error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[ManagerController] getStudyContent error {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("스터디 내용 조회에 실패했습니다.")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

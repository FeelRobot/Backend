package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.ErrorDto;
import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.dto.sign.UpdateEmailDto;
import com.feelrobot.feelrobot.dto.user.CheckPasswordDto;
import com.feelrobot.feelrobot.dto.user.ManagerResponseDto;
import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.user.UserService;
import io.netty.channel.ChannelHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "설문조사 저장 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "설문조사 저장 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<Object> survey(@RequestBody SurveyResponseDto surveyResponseDto) {
        log.info("[UserController] survey");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            userService.saveSurvey(surveyResponseDto);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(HttpStatus.OK.value())
                    .data("설문조사 저장에 성공했습니다.")
                    .build();
            return new ResponseEntity<>(responseDto,headers, HttpStatus.OK);
        } catch (ResponseException e){
            log.error("[UserController] {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] save survey error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .errorMessage("설문조사 저장에 실패했습니다.")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "학생 정보 조회 성공", content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
                    @ApiResponse(responseCode = "200", description = "학부모 정보 조회 성공", content = @Content(schema = @Schema(implementation = ManagerResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "유저 정보 조회 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
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
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] userInfo error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .errorMessage("userInfo error")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/password")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "비밀번호 확인 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    public ResponseEntity<Object> checkPassword(HttpServletRequest request, @RequestBody CheckPasswordDto checkPasswordDto) {
        log.info("[UserController] checkPassword");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try {
            String userId = (String) request.getAttribute("userId");
            boolean result = userService.checkPassword(userId, checkPasswordDto.getPassword());
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(HttpStatus.OK.value())
                    .data(result)
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[UserController] {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[UserController] checkPassword error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .errorMessage("checkPassword error")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/email")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "이메일 변경 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "이메일 변경 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
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
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(HttpStatus.OK.value())
                    .data("이메일 변경 성공")
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 이메일 변경 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .errorMessage("이메일 변경 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

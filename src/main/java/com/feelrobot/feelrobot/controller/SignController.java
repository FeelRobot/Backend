package com.feelrobot.feelrobot.controller;

import com.feelrobot.feelrobot.dto.ErrorDto;
import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.dto.sign.*;
import com.feelrobot.feelrobot.exception.RegisterDuplicationException;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.service.sign.SignService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sign")
@Tag(name = "Sign", description = "로그인, 회원가입 API")
public class SignController {

    private final SignService signService;

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> register(@RequestBody RegisterDto registerDto) {
        log.info("[SignController] 회원가입 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.register(registerDto);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("회원가입 성공")
                    .build();
            return ResponseEntity.ok(responseDto);
        } catch (RegisterDuplicationException e) {
            log.error("[SignController] {}", e.getMessage());
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(400)
                    .data(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.BAD_REQUEST);
        } catch (ResponseException e) {
            log.error("[SignController] server error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] register error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("회원가입 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        log.info("[SignController] 로그인 요청");
        try{
            return new ResponseEntity<>(signService.login(loginRequestDto), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error("[SignController] {}", e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 로그인 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("로그인 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "로그아웃 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> logout(@RequestBody String refreshToken) {
        log.info("[SignController] 로그아웃 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.logout(refreshToken);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("로그아웃 성공")
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
            log.error("[SignController] logout error");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("로그아웃 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = RefreshTokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "토큰 재발급 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshDto refreshDto) {
        log.info("[SignController] 토큰 재발급 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            return new ResponseEntity<>(signService.refreshToken(refreshDto), headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(e.getResultCode())
                    .errorMessage(e.getMessage())
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 토큰 재발급 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("토큰 재발급 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Object> kakaoLoginCallback(@RequestParam("code") String code) {
        log.info("[SignController] 카카오 로그인 콜백 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            LoginResponseDto loginResponseDto = signService.kakaoGetToken(code);
            if(Objects.equals(loginResponseDto.getRefreshToken(), "none")) {
                headers.add("Location", "feelobot://kakao?email=" + loginResponseDto.getAccessToken());
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            headers.add("Location", "feelobot://kakao?accessToken=" + loginResponseDto.getAccessToken() + "&refreshToken=" + loginResponseDto.getRefreshToken());
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 카카오 로그인 콜백 실패");
            return new ResponseEntity<>("kakao login callback error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 중복 확인 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "아이디 중복 확인 실패", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<Object> checkId(@PathVariable String id) {
        log.info("[SignController] 아이디 중복 확인 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.checkId(id);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("아이디 사용 가능")
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
        } catch (ResponseException e) {
            log.error(e.getMessage());
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(e.getResultCode())
                    .data(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, headers, e.getResultCode());
        } catch (Exception e) {
            log.error("[SignController] 아이디 중복 확인 실패");
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(500)
                    .data("아이디 중복 확인 실패")
                    .build();
            return new ResponseEntity<>(responseDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메일 전송 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "메일 전송 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> sendMail(@RequestBody MailDto mail) {
        log.info("[SignController] 메일 전송 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.sendMail(mail);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("메일 전송 성공")
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
            log.error("[SignController] 메일 전송 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("메일 전송 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check/{email}/{number}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> checkEmail(@PathVariable String email, @PathVariable int number) {
        log.info("[SignController] 이메일 인증 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.checkEmail(email, number);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("이메일 인증 성공")
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
            log.error("[SignController] 이메일 인증 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("이메일 인증 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exist/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 존재 여부 확인 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "이메일 존재 여부 확인 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> existEmail(@PathVariable String email){
        log.info("[SignController] 이메일 존재 여부 확인 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            boolean exist = signService.existId(email);

            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("인증번호가 전송되었습니다.")
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
            log.error("[SignController] 이메일 존재 여부 확인 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("이메일 존재 여부 확인 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "아이디 찾기 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> findId(@PathVariable String email){
        log.info("[SignController] 아이디 찾기 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            String id = "{\"id\" : \"" + signService.findId(email) + "\"}";
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data(id)
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
            log.error("[SignController] 아이디 찾기 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("아이디 찾기 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 확인 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "아이디 확인 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> isCorrectId(@RequestParam String id, @RequestParam String email){
        log.info("[SignController] 아이디 확인 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            boolean isCorrect = signService.isCorrectId(id, email);
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("인증번호가 전송되었습니다.")
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
            log.error("[SignController] 아이디 확인 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("아이디 확인 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto){
        log.info("[SignController] 비밀번호 변경 요청");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        try{
            signService.updatePassword(updatePasswordDto.getId(), updatePasswordDto.getPassword());
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data("비밀번호 변경 성공")
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
            log.error("[SignController] 비밀번호 변경 실패");
            ErrorDto errorDto = ErrorDto.builder()
                    .errorCode(500)
                    .errorMessage("비밀번호 변경 실패")
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .build();
            return new ResponseEntity<>(errorDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

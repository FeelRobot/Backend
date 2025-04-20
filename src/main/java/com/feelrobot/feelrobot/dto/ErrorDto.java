package com.feelrobot.feelrobot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "에러 DTO")
@Builder
public class ErrorDto {

    @Schema(description = "에러 코드")
    private int errorCode;
    @Schema(description = "에러 메시지")
    private String errorMessage;
    @Schema(description = "에러 발생 시간")
    private String timestamp;

}

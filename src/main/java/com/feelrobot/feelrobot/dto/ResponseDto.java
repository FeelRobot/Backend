package com.feelrobot.feelrobot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "응답 DTO")
@Builder
public class ResponseDto<T> {

    @Schema(description = "http 코드")
    private int HttpStatus;
    @Schema(description = "응답 메시지")
    private T data;
}

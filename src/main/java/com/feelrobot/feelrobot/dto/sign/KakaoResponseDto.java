package com.feelrobot.feelrobot.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoResponseDto {

    private String token_type;
    private String access_token;
    private String id_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
    private String scope;

}

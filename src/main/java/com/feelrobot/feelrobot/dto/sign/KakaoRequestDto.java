package com.feelrobot.feelrobot.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoRequestDto {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String code;
    private String client_secret;
}

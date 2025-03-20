package com.feelrobot.feelrobot.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {

    private String id;
    private String password;
    private String email;
    private String name;
    private int role;
}

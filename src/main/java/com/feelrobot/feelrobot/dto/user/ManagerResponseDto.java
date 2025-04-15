package com.feelrobot.feelrobot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerResponseDto {

    private String userId;
    private String email;
    private String name;
    private int role;
}

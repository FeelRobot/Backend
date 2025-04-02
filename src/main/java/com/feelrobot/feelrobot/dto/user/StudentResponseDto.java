package com.feelrobot.feelrobot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponseDto {

    private String userId;
    private String email;
    private String name;
    private String birth;
    private int sex;
    private String managerId;
}

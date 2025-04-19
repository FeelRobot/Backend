package com.feelrobot.feelrobot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyResponseDto {

    public String userId;
    public String birth;
    public int sex;
    public String managerId;

    @Override
    public String toString(){
        return "SurveyResponseDto{ userId : " + userId +" birth : " + birth + " sex : " + sex + " managerId : " + managerId + "}";
    }

}

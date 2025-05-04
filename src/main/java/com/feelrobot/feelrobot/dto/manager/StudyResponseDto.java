package com.feelrobot.feelrobot.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyResponseDto {

    public int studyId;
    public String chatBotName;
    public String difficulty;

}

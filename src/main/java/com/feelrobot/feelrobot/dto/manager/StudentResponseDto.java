package com.feelrobot.feelrobot.dto.manager;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponseDto {

    public String studentName;
    public int studentWeeklyStudyCount;

}

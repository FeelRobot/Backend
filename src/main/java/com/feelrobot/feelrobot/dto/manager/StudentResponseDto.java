package com.feelrobot.feelrobot.dto.manager;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponseDto {

    public String studentName;
    public List<StudyResponseDto> studentStudyList;

}

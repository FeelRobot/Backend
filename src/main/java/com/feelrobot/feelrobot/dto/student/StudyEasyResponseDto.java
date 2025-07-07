package com.feelrobot.feelrobot.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyEasyResponseDto {
    private String situation;
    private List<String> choices;
    private ContextDto context;
    private List<String> similar_sentences;
}

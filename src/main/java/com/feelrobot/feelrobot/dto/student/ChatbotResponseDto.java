package com.feelrobot.feelrobot.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatbotResponseDto {
    private int studyId;
    private String chatBotName;
    private int chatBotId;
}

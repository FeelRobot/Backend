package com.feelrobot.feelrobot.service.student;

import com.feelrobot.feelrobot.dto.student.ChatbotResponseDto;
import com.feelrobot.feelrobot.dto.student.StudyEasyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

import java.util.List;

public interface StudentService {
    List<ChatbotResponseDto> getChatbotList(String studentId) throws ResponseException;

    StudyEasyResponseDto startStudyEasy(String studentId, int chatbotId) throws ResponseException;
}

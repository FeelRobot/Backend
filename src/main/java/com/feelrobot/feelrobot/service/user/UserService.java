package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

import javax.swing.text.html.Option;

public interface UserService {

    void saveSurvey(SurveyResponseDto surveyResponseDto) throws ResponseException;

    Object getInfo(String userId) throws ResponseException;
}

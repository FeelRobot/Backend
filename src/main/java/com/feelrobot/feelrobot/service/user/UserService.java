package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.CheckPasswordDto;
import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

import javax.swing.text.html.Option;

public interface UserService {

    void saveSurvey(SurveyResponseDto surveyResponseDto) throws ResponseException;

    Object getInfo(String userId) throws ResponseException;

    boolean checkPassword(String id, String password) throws ResponseException;

    void updateEmail(String id, String email) throws ResponseException;
}

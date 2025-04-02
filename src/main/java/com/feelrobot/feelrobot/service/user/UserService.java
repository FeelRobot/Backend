package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

public interface UserService {

    void saveSurvey(SurveyResponseDto surveyResponseDto) throws ResponseException;
}

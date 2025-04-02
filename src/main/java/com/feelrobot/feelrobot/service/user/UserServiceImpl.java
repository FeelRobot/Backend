package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Survey;
import com.feelrobot.feelrobot.model.User;
import com.feelrobot.feelrobot.repository.SurveyRepository;
import com.feelrobot.feelrobot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;


    @Override
    public void saveSurvey(SurveyResponseDto surveyResponseDto) throws ResponseException {
        log.info("[UserServiceImpl] saveSurvey");

        User user = userRepository.findById(surveyResponseDto.getUserId()).orElseThrow(() -> new ResponseException("user not found", 400));
        if(user.getSurvey() != null) {
            throw new ResponseException("이미 설문조사를 완료하였습니다.", 400);
        }

        User manager = userRepository.findById(surveyResponseDto.getManagerId()).orElseThrow(() -> new ResponseException("manager not found", 400));

        try {
            Survey survey = Survey.builder()
                    .user(user)
                    .birth(surveyResponseDto.getBrith())
                    .sex(surveyResponseDto.getSex())
                    .managerId(surveyResponseDto.getManagerId())
                    .build();

            surveyRepository.save(survey);
        } catch (Exception e) {
            log.error("[UserServiceImpl] saveSurvey error");
            throw new IllegalArgumentException("save survey error");
        }
    }
}

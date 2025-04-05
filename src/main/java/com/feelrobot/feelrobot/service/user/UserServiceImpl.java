package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.ManagerResponseDto;
import com.feelrobot.feelrobot.dto.user.StudentResponseDto;
import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Survey;
import com.feelrobot.feelrobot.model.User;
import com.feelrobot.feelrobot.repository.SurveyRepository;
import com.feelrobot.feelrobot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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

    @Override
    public Object getInfo(String userId) throws ResponseException {
        log.info("[UserServiceImpl] getInfo");

        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseException("user not found", 400));
        if(user.getRole() == 0){
            return StudentResponseDto.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .birth(user.getSurvey().getBirth())
                    .sex(user.getSurvey().getSex())
                    .managerId(user.getSurvey().getManagerId())
                    .build();
        } else {
            return ManagerResponseDto.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();
        }
    }

    @Override
    public boolean checkPassword(String id,String password) throws ResponseException {
        log.info("[UserServiceImpl] checkPassword id:" + id + " password:" + password);

        User user = userRepository.findById(id).orElseThrow(() -> new ResponseException("user not found", 400));
        if(passwordEncoder.matches(password, user.getPassword())) {
            return true;
        } else {
            throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
        }
    }

    @Override
    public void updateEmail(String id, String email) throws ResponseException {
        log.info("[SignServiceImpl] 이메일 변경 요청");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));

        if(user.getEmail().equals(email)) {
            throw new ResponseException("변경할 이메일이 현재 이메일과 같습니다.", 400);
        }

        try {
            user.setEmail(email);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("[SignServiceImpl] 이메일 변경 실패" + e.getMessage());
            throw new RuntimeException("이메일 변경에 실패했습니다.", e);
        }
    }
}

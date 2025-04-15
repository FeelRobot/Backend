package com.feelrobot.feelrobot.service.user;

import com.feelrobot.feelrobot.dto.user.ManagerResponseDto;
import com.feelrobot.feelrobot.dto.user.StudentResponseDto;
import com.feelrobot.feelrobot.dto.user.SurveyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.*;
import com.feelrobot.feelrobot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;


    @Override
    public void saveSurvey(SurveyResponseDto surveyResponseDto) throws ResponseException {
        log.info("[UserServiceImpl] saveSurvey {}", surveyResponseDto);

        Student student = studentRepository.findById(surveyResponseDto.getUserId())
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
        if(student.getParent() != null) {
            throw new ResponseException("설문조사가 존재합니다.", 400);
        }

        Parent parent = parentRepository.findById(surveyResponseDto.getManagerId())
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));

        try {
            parent.getStudent().add(student);
            student.setParent(parent);
            student.setSex(surveyResponseDto.getSex());
            student.setBirth(surveyResponseDto.getBirth());

            studentRepository.save(student);
            parentRepository.save(parent);
        } catch (Exception e) {
            log.error("[UserServiceImpl] saveSurvey error {}", e.getMessage());
            throw new IllegalArgumentException("설문 조사 저장에 실패했습니다.");
        }
    }

    @Override
    public Object getInfo(String userId) throws ResponseException {
        log.info("[UserServiceImpl] getInfo");

        if(studentRepository.findById(userId).isPresent()) {
            Student student = studentRepository.findById(userId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            return StudentResponseDto.builder()
                    .userId(student.getStudentId())
                    .birth(student.getBirth())
                    .sex(student.getSex())
                    .email(student.getEmail())
                    .managerId(student.getParent().getParentId())
                    .role(0)
                    .build();
        } else if(parentRepository.findById(userId).isPresent()) {
            Parent parent = parentRepository.findById(userId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            return ManagerResponseDto.builder()
                    .userId(parent.getParentId())
                    .email(parent.getEmail())
                    .name(parent.getName())
                    .role(1)
                    .build();
        } else {
            throw new ResponseException("존재하지 않는 아이디입니다.", 400);
        }
    }

    @Override
    public boolean checkPassword(String id,String password) throws ResponseException {
        log.info("[UserServiceImpl] checkPassword id:" + id + " password:" + password);

        if(studentRepository.findById(id).isPresent()){
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(passwordEncoder.matches(password, student.getPassword())) {
                return true;
            } else {
                throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
            }
        } else if (parentRepository.findById(id).isPresent()) {
            Parent parent = parentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            if(passwordEncoder.matches(password, parent.getPassword())) {
                return true;
            } else {
                throw new ResponseException("비밀번호가 일치하지 않습니다.", 400);
            }
        } else {
            throw new ResponseException("존재하지 않는 아이디입니다.", 400);
        }
    }

    @Override
    public void updateEmail(String id, String email) throws ResponseException {
        log.info("[SignServiceImpl] 이메일 변경 요청");

        if(studentRepository.findById(id).isPresent()) {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            student.setEmail(email);
            studentRepository.save(student);
        } else if (parentRepository.findById(id).isPresent()) {
            Parent parent = parentRepository.findById(id)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
            parent.setEmail(email);
            parentRepository.save(parent);
        } else {
            throw new ResponseException("존재하지 않는 아이디입니다.", 400);
        }
    }
}

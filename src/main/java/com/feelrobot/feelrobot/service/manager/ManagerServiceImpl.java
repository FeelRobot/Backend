package com.feelrobot.feelrobot.service.manager;

import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Parent;
import com.feelrobot.feelrobot.repository.ChatBotRepository;
import com.feelrobot.feelrobot.repository.ParentRepository;
import com.feelrobot.feelrobot.repository.StudentRepository;
import com.feelrobot.feelrobot.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ManagerServiceImpl implements ManagerService{

    private final ParentRepository parentRepository;
    private final ChatBotRepository chatBotRepository;
    private final StudentRepository studentRepository;
    private final StudyRepository studyRepository;

    @Override
    public List<StudentResponseDto> getStudentList(String managerId) throws ResponseException {
        log.info("[getStudentList] getStudentList, input : {}", managerId);

        Parent parent = parentRepository.findById(managerId)
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
        try {

            return parent.getStudent().stream()
                    .map(student -> {
                        List<StudyResponseDto> studyResponseDtoList = student.getStudyList().stream()
                                .map(study -> {
                                    try {
                                        return StudyResponseDto.builder()
                                                .studyId(study.getStudyId())
                                                .chatBotName(chatBotRepository.findById(study.getChatBotId())
                                                        .orElseThrow(() -> new ResponseException("존재하지 않는 챗봇입니다.", 400))
                                                        .getChatBotName())
                                                .difficulty(study.getDifficulty())
                                                .build();
                                    } catch (ResponseException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .toList();

                        return StudentResponseDto.builder()
                                .studentName(student.getName())
                                .studentStudyList(studyResponseDtoList)
                                .build();
                    })
                    .toList();

        } catch (Exception e) {
            log.error("[getStudentList] getStudentList error {}", e.getMessage());
            throw new ResponseException("학생 목록 조회에 실패했습니다.", 400);
        }

    }


}

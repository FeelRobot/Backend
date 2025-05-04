package com.feelrobot.feelrobot.service.student;

import com.feelrobot.feelrobot.dto.student.ChatbotResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Student;
import com.feelrobot.feelrobot.model.Study;
import com.feelrobot.feelrobot.repository.ChatBotRepository;
import com.feelrobot.feelrobot.repository.StudentRepository;
import com.feelrobot.feelrobot.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudyRepository studyRepository;
    private final ChatBotRepository chatBotRepository;

    @Override
    public List<ChatbotResponseDto> getChatbotList(String studentId) throws ResponseException {
        log.info("[getChatbotList] getChatbotList, input : {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseException("존재하지 않는 아이디입니다.", 400));
        try {
            List<Study> studyList = student.getStudyList();
            List<ChatbotResponseDto> responseDtoList = new ArrayList<>();
            for(Study study : studyList) {
                if(study.isDeleted()) continue;
                String ChatBotName = chatBotRepository.findById(study.getChatBotId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 챗봇입니다."))
                        .getChatBotName();
                ChatbotResponseDto responseDto = ChatbotResponseDto.builder()
                        .studyId(study.getStudyId())
                        .chatBotName(ChatBotName)
                        .chatBotId(study.getChatBotId())
                        .build();
                responseDtoList.add(responseDto);
            }
            return responseDtoList;
        } catch (RuntimeException e) {
            log.error("[getChatbotList] getChatbotList error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

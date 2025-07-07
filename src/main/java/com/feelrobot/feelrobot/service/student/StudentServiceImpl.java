package com.feelrobot.feelrobot.service.student;

import com.feelrobot.feelrobot.dto.student.ChatbotResponseDto;
import com.feelrobot.feelrobot.dto.student.StudyEasyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.ChatBot;
import com.feelrobot.feelrobot.model.Student;
import com.feelrobot.feelrobot.model.Study;
import com.feelrobot.feelrobot.repository.ChatBotRepository;
import com.feelrobot.feelrobot.repository.StudentRepository;
import com.feelrobot.feelrobot.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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

    /*@Override
    public StudyEasyResponseDto startStudyEasy(String studentId, int chatbotId) throws ResponseException {
        log.info("[startStudyEasy] startStudyEasy, input : {}", studentId);

        ChatBot chatBot = chatBotRepository.findById(chatbotId)
                .orElseThrow(() -> new ResponseException("존재하지 않는 챗봇입니다.", 400));

        String url = "AI 서버 링크";

        try{
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();

            MultiValueMap<String, String> chatbotDetail = new LinkedMultiValueMap<>();
            chatbotDetail.add("name", chatBot.getChatBotName());
            chatbotDetail.add("description", chatBot.getChatBotDescription());
            chatbotDetail.add("effect", chatBot.getChatBotEffect());
            chatbotDetail.add("tag", chatBot.getChatBotTag().toString());

        } catch (RuntimeException e){
            log.error("[startStudyEasy] startStudyEasy error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }*/
}

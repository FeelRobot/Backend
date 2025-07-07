package com.feelrobot.feelrobot.service.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudyResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;
import com.feelrobot.feelrobot.model.Parent;
import com.feelrobot.feelrobot.model.Student;
import com.feelrobot.feelrobot.model.Study;
import com.feelrobot.feelrobot.repository.ChatBotRepository;
import com.feelrobot.feelrobot.repository.ParentRepository;
import com.feelrobot.feelrobot.repository.StudentRepository;
import com.feelrobot.feelrobot.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
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
                        List<StudyResponseDto> studyResponseDtoList = studyRepository.findAllByIsDeletedFalse().stream()
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

    @Override
    public void deleteStudy(String managerId, int studyId) throws ResponseException {
        log.info("[deleteStudy] deleteStudy, input : {}", studyId);

        try {
            Study study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 스터디입니다.", 400));

            if(study.isDeleted()) {
                throw new ResponseException("이미 삭제된 학습입니다.", 400);
            }

            Parent parent = parentRepository.findById(managerId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 부모님 아이디입니다.", 400));
            for(Student student : parent.getStudent()) {
                if(study.getStudent().getStudentId().equals(student.getStudentId())) {
                    study.setDeleted(true);
                    studyRepository.save(study);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("[deleteStudy] deleteStudy error {}", e.getMessage());
            throw new ResponseException("스터디 삭제에 실패했습니다.", 500);
        }
    }

    @Override
    public ResponseDto<Object> getStudyContent(String managerId, int studyId) throws ResponseException {
        log.info("[getStudyContent] getStudyContent, input : {}", studyId);

        try {
            Study study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 스터디입니다.", 400));

            if(study.isDeleted()) {
                throw new ResponseException("이미 삭제된 학습입니다.", 400);
            }

            Parent parent = parentRepository.findById(managerId)
                    .orElseThrow(() -> new ResponseException("존재하지 않는 부모님 아이디입니다.", 400));

            String filePathStr = study.getStudyFilePath() + "/" + study.getStudyFileName();
            Path filePath = Paths.get(filePathStr);

            if(!Files.exists(filePath)) {
                throw new ResponseException("존재하지 않는 파일입니다.", 400);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fileRoot = objectMapper.readTree(filePath.toFile());

            Map<String, Object> response = new HashMap<>();
            response.put("context", objectMapper.convertValue(fileRoot, new TypeReference<>() {}));

            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .HttpStatus(200)
                    .data(response)
                    .build();

            return responseDto;

        } catch (ResponseException e) {
            log.error("[getStudyContent] getStudyContent error {}", e.getMessage());
            throw new ResponseException(e.getMessage(), e.getResultCode());
        } catch (Exception e) {
            log.error("[getStudyContent] getStudyContent error {}", e.getMessage());
            throw new ResponseException("스터디 내용 조회에 실패했습니다.", 500);
        }
    }
}

package com.feelrobot.feelrobot.service.manager;

import com.feelrobot.feelrobot.dto.ResponseDto;
import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

import java.util.List;
import java.util.Map;

public interface ManagerService {

    List<StudentResponseDto> getStudentList(String managerId) throws ResponseException;

    void deleteStudy(String managerId, int studyId) throws ResponseException;

    ResponseDto<Object> getStudyContent(String managerId, int studyId) throws ResponseException;
}

package com.feelrobot.feelrobot.service.manager;

import com.feelrobot.feelrobot.dto.manager.StudentResponseDto;
import com.feelrobot.feelrobot.exception.ResponseException;

import java.util.List;

public interface ManagerService {

    List<StudentResponseDto> getStudentList(String managerId) throws ResponseException;
}

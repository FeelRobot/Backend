package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {
}

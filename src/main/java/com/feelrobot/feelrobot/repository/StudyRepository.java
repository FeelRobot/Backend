package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Integer> {
}

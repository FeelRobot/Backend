package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Integer> {
    @Query("SELECT s FROM Study s WHERE s.studyId = ?1")
    void deleteByStudyId(int studyId);

    @Query("SELECT s FROM Study s WHERE s.isDeleted = false")
    List<Study> findAllByIsDeletedFalse();
}

package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    Optional<Student> findByEmail(String email);
}

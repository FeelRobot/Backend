package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    List<Certification> findAllByCertificationEmail(String email);
}

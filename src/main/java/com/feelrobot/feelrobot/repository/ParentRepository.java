package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, String> {
    boolean existsByEmail(String email);
    boolean existsByParentId(String parentId);
    Optional<Parent> findByEmail(String email);
}

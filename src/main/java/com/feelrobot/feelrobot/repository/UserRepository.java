package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsById(String id);
}

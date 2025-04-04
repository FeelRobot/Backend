package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsById(String id);
    Optional<User> findByEmail(String email);
}

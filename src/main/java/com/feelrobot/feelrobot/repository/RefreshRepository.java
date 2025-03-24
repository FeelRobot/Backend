package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Integer> {

    @Query("SELECT r FROM Refresh r WHERE r.token = :token")
    Optional<Refresh> findByToken(String token);

    void deleteByToken(String token);
}

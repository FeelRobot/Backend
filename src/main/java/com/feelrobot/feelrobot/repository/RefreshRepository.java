package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Integer> {
    Refresh findByToken(String token);
}

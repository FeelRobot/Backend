package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {

}

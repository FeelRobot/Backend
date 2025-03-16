package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.ChatBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatBotRepository extends JpaRepository<ChatBot, Integer> {
}

package com.feelrobot.feelrobot.repository;

import com.feelrobot.feelrobot.model.ChatBotTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatBotTagRepository extends JpaRepository<ChatBotTag, Integer> {
}

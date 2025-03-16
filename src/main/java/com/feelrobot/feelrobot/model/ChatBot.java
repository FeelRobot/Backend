package com.feelrobot.feelrobot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatBotId;

    private String chatBotName;

    private String chatBotDescription;

    private String chatBotEffect;

    @OneToMany(mappedBy = "chatBotId")
    private List<ChatBotTag> chatBotTag;
}

package com.feelrobot.feelrobot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatBotTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tagId;

    private String tagContent;

    @ManyToOne
    @JoinColumn(name = "chatBotId")
    private ChatBot chatBotId;
}

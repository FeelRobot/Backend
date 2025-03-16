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
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studyId;

    private String studyFileName;

    private String difficulty;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student studentId;

    @ManyToOne
    @JoinColumn(name = "chatBotId")
    private ChatBot chatBotId;

}

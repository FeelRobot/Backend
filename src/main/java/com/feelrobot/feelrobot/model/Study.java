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

    private String studyFilePath;
    private String studyFileName;

    private String difficulty;
    private int chatBotId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

}

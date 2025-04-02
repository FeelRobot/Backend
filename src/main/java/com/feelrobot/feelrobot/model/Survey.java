package com.feelrobot.feelrobot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int surveyId;

    private String birth;

    private int sex;

    private String managerId;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
}

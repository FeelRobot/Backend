package com.feelrobot.feelrobot.model;

import jakarta.annotation.Nullable;
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
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int certificationId;

    private String certificationEmail;

    private int certificationNumber;

    @ManyToOne
    @JoinColumn(name = "studentId")
    @Nullable
    private Student studentId;

    @ManyToOne
    @JoinColumn(name = "managerId")
    @Nullable
    private Manager managerId;
}

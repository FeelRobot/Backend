package com.feelrobot.feelrobot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    private String studentId;

    private String name;

    private String birth;

    private String password;

    private int sex;

    private String email;

    @ManyToOne
    @JoinColumn(name = "parent")
    private Parent parent;
}

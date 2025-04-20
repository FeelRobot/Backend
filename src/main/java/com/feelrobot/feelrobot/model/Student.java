package com.feelrobot.feelrobot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Study> studyList;
}

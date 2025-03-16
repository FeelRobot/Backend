package com.feelrobot.feelrobot.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Manager {

    @Id
    private String id;

    private String name;

    private String password;

    private String email;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Student> students;
}

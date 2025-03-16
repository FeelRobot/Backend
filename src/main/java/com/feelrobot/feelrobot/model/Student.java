package com.feelrobot.feelrobot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Student {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "managerId")
    private Manager manager;
}

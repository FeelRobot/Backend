package com.feelrobot.feelrobot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private int role;

    @OneToOne(mappedBy = "user")
    private Survey survey;
}

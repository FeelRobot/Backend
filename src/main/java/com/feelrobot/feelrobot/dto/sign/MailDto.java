package com.feelrobot.feelrobot.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    private String email;
}

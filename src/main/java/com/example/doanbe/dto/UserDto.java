package com.example.doanbe.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String displayName;
    private String email;
    private String urlAvatar;
    private LocalDateTime createdAt;
}
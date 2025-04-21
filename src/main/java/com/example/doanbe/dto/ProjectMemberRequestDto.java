package com.example.doanbe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Service
public class ProjectMemberRequestDto {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String name;

    private String avatar;

    @NotNull(message = "Role is required")
    @Pattern(regexp = "OWNER|MEMBER|GUEST", message = "Invalid role value")
    private String role;
}

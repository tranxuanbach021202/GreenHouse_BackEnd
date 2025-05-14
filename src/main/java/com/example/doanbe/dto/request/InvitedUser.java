package com.example.doanbe.dto.request;

import com.example.doanbe.enums.ProjectRole;
import lombok.Data;

@Data
public class InvitedUser {
    private String userId;
    private ProjectRole role;
}

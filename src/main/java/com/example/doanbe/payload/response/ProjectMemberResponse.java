package com.example.doanbe.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberResponse {
    private String userId;
    private String userName;
    private String displayName;
    private String avatar;
    private String role;
}


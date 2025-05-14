package com.example.doanbe.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class InvitationRequest {
    private String projectId;
    private String projectName;
    private String inviterId;
    private String inviterName;
    private List<InvitedUser> invitedUsers;
}

package com.example.doanbe.document;


import com.example.doanbe.enums.InvitationStatus;
import com.example.doanbe.enums.ProjectRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id private String id;
    private String projectId;
    private String projectCode;
    private String projectName;
    private String thumbnailUrlProject;
    private String inviterId;
    private String inviterName;
    private String invitedUserId;
    private String invitedUserName;
    private String email;
    private String role;              // MEMBER, GUEST
    private InvitationStatus status;  // PENDING, ACCEPTED, DECLINED
    private Date createdAt;
    private Date updatedAt;
}


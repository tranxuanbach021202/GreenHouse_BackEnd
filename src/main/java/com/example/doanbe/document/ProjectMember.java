package com.example.doanbe.document;

import com.example.doanbe.enums.ProjectRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "project_members")
@Data
public class ProjectMember {
    @Id
    private String id;
    private String projectId;
    private String projectCode;
    private String projectName;
    private String userId;
    private String userName;
    private String email;
    private String urlAvatar;
    private ProjectRole role;
    private Date createdAt;
    private Date updatedAt;
}
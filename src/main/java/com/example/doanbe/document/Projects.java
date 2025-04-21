package com.example.doanbe.document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "projects")
@Data
@Getter
@Setter
public class Projects {
    @Id
    private String id;
    private String projectCode;
    private String projectName;
    private String thumbnailUrl;
    private Date startDate;
    private Date endDate;
    private String description;
    private String experimentType;
    @Field("owner")
    private ProjectOwner owner;
    private Date createdAt;
    private Date updatedAt;
}


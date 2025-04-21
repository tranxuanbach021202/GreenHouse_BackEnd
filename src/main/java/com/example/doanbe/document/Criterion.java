package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "criteria")
@Data
public class Criterion {
    @Id
    private String id;
    private String projectId;
    private String criterionCode;
    private String criterionName;
    private Date createdAt;
    private Date updatedAt;
}

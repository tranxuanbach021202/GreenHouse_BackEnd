package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "factors")
@Data
public class Factor {
    @Id
    private String id;
    private String projectId;
    private String factorCode;
    private String factorName;
    private List<Level> levels;
    private Date createdAt;
    private Date updatedAt;
}


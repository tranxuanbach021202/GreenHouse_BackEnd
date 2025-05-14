package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "treatments")
@Data
public class Treatment {
    @Id
    private String id;
    private String projectId;
    private String treatmentCode;
    private String treatmentName;
    private String factorId;
    private String levelId;
    private Date createdAt;
    private Date updatedAt;
}
package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
public class Level {
    private String id;
    private String levelCode;
    private String levelName;

    private Date createdAt;
    private Date updatedAt;
}

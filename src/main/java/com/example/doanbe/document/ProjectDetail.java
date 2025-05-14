package com.example.doanbe.document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Document(collection = "projects_detail")
@Data
@Getter
@Setter
public class ProjectDetail {
    @Id
    private String id;
    private String projectId;
    private String projectCode;
    private String projectName;
    private String thumbnailUrl;
    private String description;
    private List<Criterion> criterionList;
    private List<Treatment> treatmentList;
    private Factor factor;
    @Field("owner")
    private ProjectOwner owner;
    private String experimentType;
    private int block;
    private int replicate;
    private int column;
}

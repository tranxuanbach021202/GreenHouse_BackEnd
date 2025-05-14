package com.example.doanbe.document;

import com.example.doanbe.enums.TypeExperiment;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "experiments")
@Data
@Getter
public class Experiment {
    @Id
    private String id;
    private String projectId;
    private Integer blocks;
    private Integer replicates;
    private Integer columns;
    private String type;
    private List<Treatment> treatments;
    private List<List<Plot>> layout;
    private Date createdAt;
    private Date updatedAt;

}
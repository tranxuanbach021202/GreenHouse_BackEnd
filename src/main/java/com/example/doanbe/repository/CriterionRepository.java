package com.example.doanbe.repository;

import com.example.doanbe.document.Criterion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CriterionRepository extends MongoRepository<Criterion, String> {
    List<Criterion> findByProjectId(String projectId);
    void deleteByProjectId(String projectId);
}

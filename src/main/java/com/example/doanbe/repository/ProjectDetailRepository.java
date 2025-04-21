package com.example.doanbe.repository;

import com.example.doanbe.document.ProjectDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectDetailRepository extends MongoRepository<ProjectDetail, String> {
    Optional<ProjectDetail> findByProjectCode(String code);
}

package com.example.doanbe.repository;

import com.example.doanbe.document.ProjectDetail;
import com.example.doanbe.document.Projects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectDetailRepository extends MongoRepository<ProjectDetail, String> {
    Optional<ProjectDetail> findByProjectCode(String code);
    Optional<ProjectDetail> findByProjectId(String projectId);
    List<ProjectDetail> findByProjectIdIn(List<String> projectIds);
    Page<ProjectDetail> findByProjectIdIn(List<String> projectIds, Pageable pageable);
    void deleteByProjectId(String projectId);


}

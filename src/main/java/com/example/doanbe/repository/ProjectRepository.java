package com.example.doanbe.repository;

import com.example.doanbe.document.Projects;
import com.example.doanbe.document.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Projects, String> {
    Page<Projects> findAllByOwner_Id(String ownerId, Pageable pageable);
    Optional<Projects> findById(String id);
    Optional<Projects> findByProjectCode(String projectCode);
    Page<Projects> findByPublicVisibleTrue(Pageable pageable);
    List<Projects> findByIdIn(List<String> ids);




}

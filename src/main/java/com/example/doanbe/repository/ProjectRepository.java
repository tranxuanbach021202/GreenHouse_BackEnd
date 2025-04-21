package com.example.doanbe.repository;

import com.example.doanbe.document.Projects;
import com.example.doanbe.document.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Projects, String> {
    Page<Projects> findAllByOwner_Id(String ownerId, Pageable pageable);
    Optional<Projects> findById(String id);

}

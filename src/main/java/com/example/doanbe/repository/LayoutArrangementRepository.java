package com.example.doanbe.repository;

import com.example.doanbe.document.Experiment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LayoutArrangementRepository extends MongoRepository<Experiment, String> {
    Optional<Experiment> findByProjectId(String projectId);
}

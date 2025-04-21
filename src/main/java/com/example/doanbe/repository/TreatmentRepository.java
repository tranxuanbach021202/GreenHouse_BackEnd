package com.example.doanbe.repository;

import com.example.doanbe.document.Treatment;
import com.example.doanbe.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TreatmentRepository extends MongoRepository<Treatment, String> {
    List<Treatment> findByProjectId(String projectId);
}

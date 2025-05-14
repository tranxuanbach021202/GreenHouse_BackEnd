package com.example.doanbe.repository;

import com.example.doanbe.document.Treatment;
import com.example.doanbe.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TreatmentRepository extends MongoRepository<Treatment, String> {
    Optional<Treatment> findByProjectIdAndTreatmentCode(String projectId, String treatmentCode);
    List<Treatment> findByProjectId(String projectId);
    Optional<Treatment> findByLevelId(String levelId);

}

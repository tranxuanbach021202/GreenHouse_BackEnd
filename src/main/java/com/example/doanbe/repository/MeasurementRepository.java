package com.example.doanbe.repository;

import com.example.doanbe.document.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends MongoRepository<Measurement, String> {
    List<Measurement> findByProjectId(String projectId);
}


package com.example.doanbe.repository;

import com.example.doanbe.document.MeasurementHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MeasurementHistoryRepository extends MongoRepository<MeasurementHistory, String> {
    void deleteByMeasurementId(String measurementId);
    List<MeasurementHistory> findByMeasurementId(String measurementId);
    List<MeasurementHistory> findByMeasurementIdOrderByTimestampDesc(String measurementId);
    List<MeasurementHistory> findByEditSessionId(String editSessionId);
    void deleteByMeasurementIdIn(List<String> measurementIds);



}
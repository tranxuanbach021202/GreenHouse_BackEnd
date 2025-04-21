package com.example.doanbe.services;

import com.example.doanbe.document.Measurement;
import com.example.doanbe.dto.request.MeasurementRequest;
import com.example.doanbe.dto.response.MeasurementDetailDto;

import java.util.List;

public interface MeasurementService {
    Measurement addMeasurement(MeasurementRequest request);
    List<Measurement> getMeasurementsByProjectId(String projectId);
    MeasurementDetailDto getMeasurementDetail(String id);
}

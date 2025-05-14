package com.example.doanbe.services;

import com.example.doanbe.document.Measurement;
import com.example.doanbe.dto.request.*;
import com.example.doanbe.dto.response.EditSessionHistoryResponse;
import com.example.doanbe.dto.response.MeasurementDetailDto;
import com.example.doanbe.dto.response.MeasurementHistoryResponse;

import java.util.List;

public interface MeasurementService {
    Measurement createMeasurement(CreateMeasurementRequest request);

    void deleteMeasurementById(String id);

    void updateMeasurement(String id, UpdateMeasurementRequest request);

    Measurement addMeasurement(MeasurementRequest request, String projectId);

    Measurement createMeasurement(NewMeasurementRequest request);

    List<Measurement> getMeasurementsByProjectId(String projectId);
    MeasurementDetailDto getMeasurementDetail(String id);

    Measurement updateMeasurementData(String id, UpdateMeasurementDataRequest request) throws Exception;

    List<EditSessionHistoryResponse> getGroupedHistory(String measurementId);

    byte[] exportToExcel(String projectId);
}

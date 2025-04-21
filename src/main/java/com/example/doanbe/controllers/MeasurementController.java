package com.example.doanbe.controllers;

import com.example.doanbe.document.Measurement;
import com.example.doanbe.dto.request.MeasurementRequest;
import com.example.doanbe.dto.response.MeasurementDetailDto;
import com.example.doanbe.dto.response.MeasurementResponseDto;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/measurement")
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;


    @PostMapping
    public ResponseEntity<Measurement> createEntry(@RequestBody MeasurementRequest request) {
        Measurement savedEntry = measurementService.addMeasurement(request);
        return ResponseEntity.ok(savedEntry);
    }

    @GetMapping("/project/{projectId}")
    public  ResponseEntity<SuccessResponse> getMeasurementsByProjectId(@PathVariable String projectId) {

        List<MeasurementResponseDto> measurementResponseDtos = measurementService.getMeasurementsByProjectId(projectId).stream()
                .map(m -> MeasurementResponseDto.builder()
                        .id(m.getId())
                        .projectId(m.getProjectId())
                        .userId(m.getUserId())
                        .start(m.getStart())
                        .end(m.getEnd())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new SuccessResponse(measurementResponseDtos));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<SuccessResponse> getMeasurementById(@PathVariable String id) {
        MeasurementDetailDto measurementDetailDto =  measurementService.getMeasurementDetail(id);
        return ResponseEntity.ok(new SuccessResponse(measurementDetailDto));
    }
}

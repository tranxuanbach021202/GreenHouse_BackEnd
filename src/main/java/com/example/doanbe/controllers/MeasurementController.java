package com.example.doanbe.controllers;

import com.example.doanbe.document.Measurement;
import com.example.doanbe.dto.request.*;
import com.example.doanbe.dto.response.EditSessionHistoryResponse;
import com.example.doanbe.dto.response.MeasurementDetailDto;
import com.example.doanbe.dto.response.MeasurementHistoryResponse;
import com.example.doanbe.dto.response.MeasurementResponseDto;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
    public Measurement create(@RequestBody CreateMeasurementRequest request) {
        return measurementService.createMeasurement(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateMeasurement(
            @PathVariable String id,
            @RequestBody UpdateMeasurementRequest request
    ) {
        measurementService.updateMeasurement(id, request);
        return ResponseEntity.ok(new MessageResponse("Cập nhật đợt nhập thành công"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMeasurement(@PathVariable String id) {
        measurementService.deleteMeasurementById(id);
        return ResponseEntity.ok(new MessageResponse("Xóa đợt nhập và lịch sử thành công"));
    }






    @PutMapping("/{id}/data")
    public ResponseEntity<Measurement> updateMeasurementRecords(
            @PathVariable String id,
            @RequestBody UpdateMeasurementDataRequest request) throws Exception {

        Measurement updated = measurementService.updateMeasurementData(id, request);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{id}/history")
    public ResponseEntity<?> getMeasurementHistory(@PathVariable String id) {
        List<EditSessionHistoryResponse> history = measurementService.getGroupedHistory(id);
        SuccessResponse response = new SuccessResponse(0, "Thành công!", history, null);
        return ResponseEntity.ok(response);
    }






    @PostMapping("/{id}/create-record")
    public ResponseEntity<Measurement> createEntry(
            @PathVariable("id") String measurementId,
            @RequestBody MeasurementRequest request) {

        Measurement savedEntry = measurementService.addMeasurement(request, measurementId);

        return ResponseEntity.ok(savedEntry);
    }


    @PostMapping("/new")
    public ResponseEntity<?> createNewMeasurement(@RequestBody NewMeasurementRequest request) {
        if (request.getProjectId() == null || request.getStart() == null || request.getEnd() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin: projectId, start hoặc end");
        }

        Measurement created = measurementService.createMeasurement(request);
        return ResponseEntity.ok(new MessageResponse("Tạo đợt nhập thành công"));
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


    @GetMapping("/export/data")
    public ResponseEntity<byte[]> exportMeasurements(@RequestParam String projectId) {
        byte[] excelFile = measurementService.exportToExcel(projectId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("measurements.xlsx").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Measurement> updateMeasurement(@PathVariable String id, @RequestBody Measurement measurement) {
//        try {
//            Measurement updatedMeasurement = measurementService.updateMeasurement(id, measurement);
//            return ResponseEntity.ok(updatedMeasurement);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
}

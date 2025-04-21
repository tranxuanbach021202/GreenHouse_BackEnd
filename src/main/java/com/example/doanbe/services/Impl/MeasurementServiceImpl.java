package com.example.doanbe.services.Impl;

import com.example.doanbe.document.Criterion;
import com.example.doanbe.document.Measurement;
import com.example.doanbe.document.User;
import com.example.doanbe.dto.UserDto;
import com.example.doanbe.dto.request.MeasurementRequest;
import com.example.doanbe.dto.response.MeasurementDetailDto;
import com.example.doanbe.repository.CriterionRepository;
import com.example.doanbe.repository.MeasurementRepository;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.services.MeasurementService;
import com.example.doanbe.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CriterionRepository criterionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Measurement addMeasurement(MeasurementRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Measurement entry = new Measurement();
        entry.setProjectId(request.getProjectId());
        entry.setUserId(userDetails.getId());
        entry.setStart(request.getStart());
        entry.setEnd(request.getEnd());
        entry.setCreatedAt(LocalDateTime.now());
        entry.setRecords(request.getRecords().stream().map(recordDto -> {
            Measurement.Record record = new Measurement.Record();
            record.setBlockIndex(recordDto.getBlockIndex());
            record.setPlotIndex(recordDto.getPlotIndex());
            record.setTreatmentCode(recordDto.getTreatmentCode());
            record.setValues(recordDto.getValues().stream().map(valueDto -> {
                Measurement.Value value = new Measurement.Value();
                value.setCriterionCode(valueDto.getCriterionCode());
                value.setCriterionName(valueDto.getCriterionName());
                value.setValue(valueDto.getValue());
                return value;
            }).toList());
            return record;
        }).toList());

        return measurementRepository.save(entry);
    }

    @Override
    public List<Measurement> getMeasurementsByProjectId(String projectId) {
        return measurementRepository.findByProjectId(projectId);
    }

    @Override
    public MeasurementDetailDto getMeasurementDetail(String id) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found"));

        User user = userRepository.findById(measurement.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Criterion> criterionList = criterionRepository.findByProjectId(measurement.getProjectId());

        MeasurementDetailDto dto = modelMapper.map(measurement, MeasurementDetailDto.class);

        dto.setCriterionList(criterionList);
        if (user != null) {
            dto.setCreatedBy(modelMapper.map(user, UserDto.class));
        }
        return dto;
    }

}

package com.example.doanbe.services.Impl;

import com.example.doanbe.document.*;
import com.example.doanbe.dto.UserDto;
import com.example.doanbe.dto.request.*;
import com.example.doanbe.dto.response.EditSessionHistoryResponse;
import com.example.doanbe.dto.response.MeasurementDetailDto;
import com.example.doanbe.repository.*;
import com.example.doanbe.services.MeasurementService;
import com.example.doanbe.services.UserDetailsImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CriterionRepository criterionRepository;

    @Autowired
    private MeasurementHistoryRepository measurementHistoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Measurement createMeasurement(CreateMeasurementRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Measurement measurement = new Measurement();
        measurement.setProjectId(request.getProjectId());
        measurement.setUserId(userDetails.getId());
        measurement.setStart(request.getStart());
        measurement.setEnd(request.getEnd());
        measurement.setCreatedAt(LocalDateTime.now());
        measurement.setRecords(null);

        return measurementRepository.save(measurement);
    }


    @Override
    public void deleteMeasurementById(String id) {

        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement không tồn tại"));


        measurementRepository.deleteById(id);


        measurementHistoryRepository.deleteByMeasurementId(id);
    }

    @Override
    public void updateMeasurement(String id, UpdateMeasurementRequest request) {
        Measurement existing = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement không tồn tại"));

        existing.setStart(request.getStart());
        existing.setEnd(request.getEnd());

        measurementRepository.save(existing);
    }


    @Override
    public Measurement addMeasurement(MeasurementRequest request, String measurementId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();


        Measurement measurement = measurementRepository.findById(measurementId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt nhập"));

        List<Measurement.Record> existingRecords = Optional.ofNullable(measurement.getRecords())
                .orElse(new ArrayList<>());

        String editSessionId = UUID.randomUUID().toString();

        List<Measurement.Record> newRecords = request.getRecords().stream().map(recordDto -> {
            Measurement.Record record = new Measurement.Record();
            record.setBlockIndex(recordDto.getBlockIndex());
            record.setPlotIndex(recordDto.getPlotIndex());
            record.setTreatmentCode(recordDto.getTreatmentCode());

            List<Measurement.Value> values = recordDto.getValues().stream().map(valueDto -> {
                Measurement.Value value = new Measurement.Value();
                value.setCriterionCode(valueDto.getCriterionCode());
                value.setCriterionName(valueDto.getCriterionName());
                value.setValue(valueDto.getValue());
                return value;
            }).toList();

            record.setValues(values);

            values.forEach(val -> saveHistory(measurementId, record, val, 0.0, editSessionId));

            return record;
        }).toList();


        existingRecords.addAll(newRecords);
        measurement.setRecords(existingRecords);

        return measurementRepository.save(measurement);
    }

    @Override
    public Measurement createMeasurement(NewMeasurementRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String userId = userDetails.getId();
        String editSessionId = UUID.randomUUID().toString();

        Measurement measurement = new Measurement();
        measurement.setId(UUID.randomUUID().toString());
        measurement.setProjectId(request.getProjectId());
        measurement.setUserId(userId);
        measurement.setStart(request.getStart());
        measurement.setEnd(request.getEnd());
        measurement.setCreatedAt(LocalDateTime.now());

        List<Measurement.Record> records = request.getRecords().stream().map(recordDto -> {
            Measurement.Record record = new Measurement.Record();
            record.setBlockIndex(recordDto.getBlockIndex());
            record.setPlotIndex(recordDto.getPlotIndex());
            record.setTreatmentCode(recordDto.getTreatmentCode());

            List<Measurement.Value> values = recordDto.getValues().stream().map(valueDto -> {
                Measurement.Value value = new Measurement.Value();
                value.setCriterionCode(valueDto.getCriterionCode());
                value.setCriterionName(valueDto.getCriterionName());
                value.setValue(valueDto.getValue());
                return value;
            }).toList();

            record.setValues(values);

            values.forEach(val -> saveHistory(measurement.getId(), record, val, 0.0, editSessionId));

            return record;
        }).toList();

        measurement.setRecords(records);

        return measurementRepository.save(measurement);
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


    @Override
    public Measurement updateMeasurementData(String id, UpdateMeasurementDataRequest request) throws Exception {
        Measurement existingMeasurement = measurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Measurement not found"));

        List<Measurement.Record> newRecords = request.getRecords();

        // Tạo id đợt chỉnh sửa
        String editSessionId = UUID.randomUUID().toString();

        for (Measurement.Record newRecord : newRecords) {
            Measurement.Record oldRecord = findRecord(existingMeasurement, newRecord);

            for (Measurement.Value newVal : newRecord.getValues()) {
                Measurement.Value oldVal = findValue(oldRecord, newVal);

                if (oldVal != null && oldVal.getValue() != newVal.getValue()) {
                    saveHistory(existingMeasurement.getId(), newRecord, newVal, oldVal.getValue(), editSessionId);
                }
            }
        }

        existingMeasurement.setRecords(newRecords);
        return measurementRepository.save(existingMeasurement);
    }



    private Measurement.Record findRecord(Measurement existingMeasurement, Measurement.Record newRecord) {
        return existingMeasurement.getRecords().stream()
                .filter(r -> r.getBlockIndex() == newRecord.getBlockIndex()
                        && r.getPlotIndex() == newRecord.getPlotIndex())
                .findFirst()
                .orElse(null);
    }


    private Measurement.Value findValue(Measurement.Record oldRecord, Measurement.Value newVal) {
        if (oldRecord == null) return null;
        return oldRecord.getValues().stream()
                .filter(v -> v.getCriterionCode().equals(newVal.getCriterionCode()))
                .findFirst()
                .orElse(null);
    }

    private void saveHistory(String measurementId, Measurement.Record record, Measurement.Value newVal, double oldValue, String editSessionId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();




        MeasurementHistory history = new MeasurementHistory();
        history.setMeasurementId(measurementId);
        history.setBlockIndex(record.getBlockIndex());
        history.setPlotIndex(record.getPlotIndex());
        history.setCriterionCode(newVal.getCriterionCode());
        history.setCriterionName(newVal.getCriterionName());
        history.setOldValue(oldValue);
        history.setNewValue(newVal.getValue());
        history.setUserId(userDetails.getId());
        history.setAction("UPDATE");
        history.setTimestamp(LocalDateTime.now());
        history.setEditSessionId(editSessionId);
        measurementHistoryRepository.save(history);
    }


    @Override
    public List<EditSessionHistoryResponse> getGroupedHistory(String measurementId) {
        List<MeasurementHistory> allHistories = measurementHistoryRepository
                .findByMeasurementIdOrderByTimestampDesc(measurementId);

        // Nhóm theo editSessionId
        Map<String, List<MeasurementHistory>> grouped = allHistories.stream()
                .collect(Collectors.groupingBy(MeasurementHistory::getEditSessionId));

        List<EditSessionHistoryResponse> result = new ArrayList<>();

        for (Map.Entry<String, List<MeasurementHistory>> entry : grouped.entrySet()) {
            List<MeasurementHistory> group = entry.getValue();
            if (group.isEmpty()) continue;

            // Lấy user info từ userId
            String userId = group.get(0).getUserId();
            User user = userRepository.findById(userId).orElse(null); // Hoặc dùng projection

            EditSessionHistoryResponse session = new EditSessionHistoryResponse();
            session.setEditSessionId(entry.getKey());
            session.setTimestamp(group.get(0).getTimestamp());
            session.setUserId(userId);
            session.setUsername(user != null ? user.getUsername() : "Unknown");
            session.setUrlAvatar(user != null ? user.getUrlAvatar() : null);

            List<EditSessionHistoryResponse.ChangeDetail> changes = group.stream().map(h -> {
                EditSessionHistoryResponse.ChangeDetail detail = new EditSessionHistoryResponse.ChangeDetail();
                detail.setBlockIndex(h.getBlockIndex());
                detail.setPlotIndex(h.getPlotIndex());
                detail.setCriterionCode(h.getCriterionCode());
                detail.setCriterionName(h.getCriterionName());
                detail.setOldValue(h.getOldValue());
                detail.setNewValue(h.getNewValue());
                return detail;
            }).collect(Collectors.toList());

            session.setChanges(changes);
            result.add(session);
        }

        // Sắp xếp theo thời gian giảm dần
        result.sort(Comparator.comparing(EditSessionHistoryResponse::getTimestamp).reversed());

        return result;
    }


    @Override
    public byte[] exportToExcel(String projectId) {

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Treatment> treatments = treatmentRepository.findByProjectId(projectId);
        List<Criterion> criteria = criterionRepository.findByProjectId(projectId);

        List<Measurement> measurements = measurementRepository.findByProjectId(projectId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Project Data");
            int rowNum = 0;

            // Đặt sau khi tạo workbook
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorderAll(headerStyle);

            CellStyle greenHeaderStyle = workbook.createCellStyle();
            greenHeaderStyle.cloneStyleFrom(headerStyle);
            greenHeaderStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());

            CellStyle tableCellStyle = workbook.createCellStyle();
            setBorderAll(tableCellStyle);

            Row codeRow = sheet.createRow(rowNum++);
            Cell codeCell = codeRow.createCell(0);
            codeCell.setCellValue("Project Code: " + project.getProjectCode());
            codeCell.setCellStyle(greenHeaderStyle);

            Row idRow = sheet.createRow(rowNum++);
            Cell idCell = idRow.createCell(0);
            idCell.setCellValue("Project ID: " + project.getId());
            idCell.setCellStyle(greenHeaderStyle);

            sheet.addMergedRegion(new CellRangeAddress(codeRow.getRowNum(), codeRow.getRowNum(), 0, 4));
            sheet.addMergedRegion(new CellRangeAddress(idRow.getRowNum(), idRow.getRowNum(), 0, 4));


            rowNum++;

            Row treatmentTitle = sheet.createRow(rowNum++);
            Cell titleCell = treatmentTitle.createCell(0);
            titleCell.setCellValue("Treatment");
            titleCell.setCellStyle(greenHeaderStyle);

            Row treatmentHeader = sheet.createRow(rowNum++);
            treatmentHeader.createCell(0).setCellValue("Mã Code");
            treatmentHeader.createCell(1).setCellValue("Chi tiết");

            for (Treatment t : treatments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getTreatmentCode());
                row.createCell(1).setCellValue(t.getTreatmentName());
            }

            rowNum++;
            Row treatmentTitle1 = sheet.createRow(rowNum++);
            Cell titleCell2 = treatmentTitle1.createCell(0);
            titleCell2.setCellValue("Critaion");
            titleCell2.setCellStyle(greenHeaderStyle);

            Row criterionHeader = sheet.createRow(rowNum++);
            criterionHeader.createCell(0).setCellValue("Mã Code");
            criterionHeader.createCell(1).setCellValue("Chi tiết");

            for (Criterion c : criteria) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getCriterionCode());
                row.createCell(1).setCellValue(c.getCriterionName());
            }

            rowNum++;

            for (Measurement m : measurements) {
                Row mIdRow = sheet.createRow(rowNum++);
                mIdRow.createCell(0).setCellValue("Measurement ID: " + m.getId());

                Row timeRow = sheet.createRow(rowNum++);
                timeRow.createCell(0).setCellValue("Start: " + m.getStart());
                timeRow.createCell(1).setCellValue("End: " + m.getEnd());

                rowNum++;

                Row header = sheet.createRow(rowNum++);
                String[] headers = {"Block", "PlotIndex", "Treatment", "Critaion Code", "Critaion Value"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }


                for (Measurement.Record record : m.getRecords()) {
                    for (Measurement.Value val : record.getValues()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(record.getBlockIndex());
                        row.createCell(1).setCellValue(record.getPlotIndex());
                        row.createCell(2).setCellValue(record.getTreatmentCode());
                        row.createCell(3).setCellValue(val.getCriterionCode());
                        row.createCell(4).setCellValue(val.getValue());
                    }
                }

                rowNum += 2;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel", e);
        }
    }

    private void setBorderAll(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }











}

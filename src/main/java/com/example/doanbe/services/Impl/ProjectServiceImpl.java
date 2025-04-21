package com.example.doanbe.services.Impl;

import com.example.doanbe.document.*;
import com.example.doanbe.dto.*;
import com.example.doanbe.enums.ProjectRole;
import com.example.doanbe.exception.AppException;
import com.example.doanbe.payload.request.CreateProjectRequest;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.*;
import com.example.doanbe.repository.*;
import com.example.doanbe.services.ProjectService;
import com.example.doanbe.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private FactorRepository factorRepository;

    @Autowired
    private CriterionRepository criterionRepository;

    @Autowired
    private LayoutArrangementRepository layoutArrangementRepository;

    @Autowired
    private ProjectDetailRepository projectDetailRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public MessageResponse createProject(CreateProjectRequest request) {
        try {
            Optional<ProjectMemberRequestDto> owner = request.getMembers().stream()
                    .filter(member -> "OWNER".equalsIgnoreCase(member.getRole())) // Lọc OWNER
                    .findFirst();
            ProjectOwner projectOwner = new ProjectOwner();
            owner.ifPresent( o -> {
                        projectOwner.setId(o.getUserId());
                        projectOwner.setAvatar(o.getAvatar());
                        projectOwner.setUserName(o.getName());
                        projectOwner.setDisplayName(o.getName());
                    }
            );

            // create project
            Projects project = new Projects();
            project.setProjectCode(request.getProjectCode());
            project.setProjectName(request.getProjectName());
            project.setStartDate(request.getStartDate());
            project.setEndDate(request.getEndDate());
            project.setDescription(request.getDescription());
            project.setThumbnailUrl(request.getThumbnailUrl());
            project.setOwner(projectOwner);
            project.setExperimentType(request.getExperimentType());
            project.setCreatedAt(new Date());
            project.setUpdatedAt(new Date());


            Projects savedProject = projectRepository.save(project);

            List<ProjectMember> members = createProjectMembers(savedProject, request.getMembers());

            Factor factor = createFactor(request.getFactor());

            List<Treatment> treatments = createTreatments(savedProject, factor);

            List<Criterion> criterions = createCriterion(savedProject, request.getCriteria());



            List<List<String>> layouts = request.getLayout();
            List<List<Plot>> parsedLayout = new ArrayList<>();

            for (List<String> blockData : layouts) {
                List<Plot> plotList = blockData.stream()
                        .map(code -> new Plot(code, null))
                        .collect(Collectors.toList());
                parsedLayout.add(plotList);
            }

            Experiment _layoutArrangement = new Experiment();
            _layoutArrangement.setProjectId(savedProject.getId());
            _layoutArrangement.setBlocks(request.getBlocks());
            _layoutArrangement.setReplicates(request.getReplicates());
            _layoutArrangement.setColumns(request.getColumns());
            _layoutArrangement.setTreatments(treatments);
            _layoutArrangement.setType(request.getExperimentType());
            _layoutArrangement.setCreatedAt(new Date());
            _layoutArrangement.setUpdatedAt(new Date());
            _layoutArrangement.setLayout(parsedLayout);
            Experiment layoutArrangement = layoutArrangementRepository.save(_layoutArrangement);

            //create project detail
            ProjectDetail projectDetail = new ProjectDetail();
            projectDetail.setProjectId(savedProject.getId());
            projectDetail.setProjectCode(request.getProjectCode());
            projectDetail.setProjectName(request.getProjectName());
            projectDetail.setCriterionList(criterions);
            projectDetail.setTreatmentList(treatments);
            projectDetail.setFactor(factor);
            projectDetail.setThumbnailUrl(request.getThumbnailUrl());
            projectDetail.setOwner(projectOwner);
            projectDetail.setBlock(request.getBlocks());
            projectDetail.setReplicate(request.getReplicates());
            projectDetail.setColumn(request.getColumns());
            projectDetail.setExperimentType(request.getExperimentType());
            projectDetailRepository.save(projectDetail);
            return new MessageResponse("Tạo project thành công");

        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo project " + e.getMessage());
        }
    }

    @Override
    public SuccessResponse getProjectsForCurrentUser(UserPagingRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Sort sort = Sort.by(
                request.getSortDir().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSortBy()
        );

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Lấy các project theo owner._id
        Page<Projects> projectPage = projectRepository.findAllByOwner_Id(userDetails.getId(), pageable);

        List<ProjectResponse> projectResponses = projectPage.getContent().stream()
                .map(project -> modelMapper.map(project, ProjectResponse.class))
                .collect(Collectors.toList());

        Pagination meta = new Pagination();
        meta.setPage(projectPage.getNumber());
        meta.setSize(projectPage.getSize());
        meta.setTotalElements(projectPage.getTotalElements());
        meta.setTotalPages(projectPage.getTotalPages());
        meta.setHasNext(projectPage.hasNext());
        meta.setHasPrevious(projectPage.hasPrevious());

        return new SuccessResponse(projectResponses, meta);
    }

    public ProjectDetailResponse getProjectDetailById(String projectId) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(404, 44,
                        "Không tồn tại project"));

        ProjectDetail detail = projectDetailRepository.findByProjectCode(project.getProjectCode())
                .orElseThrow(() -> new AppException(404, 44,
                        "Không tồn tại project detail"));

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        List<Treatment> treatments = treatmentRepository.findByProjectId(projectId);

        Factor factor = factorRepository.findById(treatments.get(0).getFactorId())
                .orElseThrow(() -> new AppException(404, 44,
                        "Không tồn tại factor"));

        Experiment experiment = layoutArrangementRepository.findByProjectId(projectId)
                .orElseThrow(() -> new AppException(404, 44,
                        "Không tồn tại layout"));;

        ProjectDetailResponse response = new ProjectDetailResponse();
        response.setId(project.getId());
        response.setCode(project.getProjectCode());
        response.setName(project.getProjectName());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setDescription(project.getDescription());
        response.setThumbnailUrl(detail.getThumbnailUrl());
        response.setExperimentType(detail.getExperimentType());
        response.setBlocks(detail.getBlock());
        response.setReplicates(detail.getReplicate());
        response.setColumns(detail.getColumn());
        response.setLayout(experiment.getLayout());
        response.setFactor(factor);
        response.setTreatments(treatments);
        response.setCriteria(detail.getCriterionList());
        response.setMembers(members.stream()
                .map(m -> new
                        ProjectMemberResponse(
                                m.getUserId(),
                                m.getUserName(),
                                "",
                                "",
                                m.getRole().toString()))
                .collect(Collectors.toList()));
        return response;
    }


    private List<ProjectMember> createProjectMembers(Projects project, List<ProjectMemberRequestDto> memberRequests) {
        List<ProjectMember> membersProject = new ArrayList<>();
        ProjectMember ownerMember = new ProjectMember();

        if (memberRequests != null) {
            for (ProjectMemberRequestDto memberRequest : memberRequests) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getId());
                member.setProjectCode(project.getProjectCode());
                member.setUserName(memberRequest.getName());
                member.setProjectName(project.getProjectName());
                member.setUserId(memberRequest.getUserId());
                member.setRole(ProjectRole.valueOf(memberRequest.getRole()));
                member.setCreatedAt(new Date());
                member.setUpdatedAt(new Date());
                membersProject.add(member);
            }
        }

        return projectMemberRepository.saveAll(membersProject);
    }
    // project

    private List<Treatment> createTreatments(Projects project, Factor factor) {
        List<Treatment> treatments = new ArrayList<>();
        if (factor != null && factor.getLevels() != null) {
            for (Level level : factor.getLevels()) {
                Treatment treatment = new Treatment();
                treatment.setProjectId(project.getId());
                treatment.setTreatmentCode(level.getLevelCode());
                treatment.setTreatmentName(level.getLevelName());
                treatment.setFactorId(factor.getId());
                treatment.setLevelId(level.getId());
                treatment.setCreatedAt(new Date());
                treatment.setUpdatedAt(new Date());
                treatments.add(treatment);
            }
        }
        return treatmentRepository.saveAll(treatments);
    }


    private Factor createFactor(FactorRequestDto factorRequest) {
            Factor factor = new Factor();
            if (factorRequest!= null) {
                List<Level> levels = new ArrayList<>();
                for (LevelRequestDto levelRequest : factorRequest.getLevels()) {
                    Level level = new Level();
                    level.setLevelCode(levelRequest.getCode());
                    level.setLevelName(levelRequest.getName());
                    levels.add(level);
                }
                factor.setFactorCode(factorRequest.getCode());
                factor.setFactorName(factorRequest.getName());
                factor.setCreatedAt(new Date());
                factor.setUpdatedAt(new Date());
                factor.setLevels(levels);
            }
            factor = factorRepository.save(factor);
            return factor;
    }

    private List<Criterion> createCriterion(Projects projects, List<CriterionRequestDto> criterionsRequest) {
           List<Criterion> criterions = new ArrayList<>();
           if (criterionsRequest != null) {
               for (CriterionRequestDto criterion : criterionsRequest) {
                   Criterion criterionObject = new Criterion();
                   criterionObject.setProjectId(projects.getId());
                   criterionObject.setCriterionCode(criterion.getCriterionCode());
                   criterionObject.setCriterionName(criterion.getCriterionName());
                   criterionObject.setCreatedAt(new Date());
                   criterionObject.setUpdatedAt(new Date());
                   criterions.add(criterionObject);
               }
           }
           return criterionRepository.saveAll(criterions);
    }







}

package com.example.doanbe.services.Impl;

import com.example.doanbe.document.*;
import com.example.doanbe.dto.*;
import com.example.doanbe.dto.request.CriterionRequest;
import com.example.doanbe.dto.request.UpdateFactorAndCriterionRequest;
import com.example.doanbe.dto.request.UpdateProjectMembersRequest;
import com.example.doanbe.dto.request.UpdateProjectRequest;
import com.example.doanbe.enums.InvitationStatus;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private InvitationRepository invitationRepository;

    @Autowired
    private InvitationServiceImpl invitationService;

    @Autowired
    private MeasurementHistoryRepository measurementHistoryRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

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
                        projectOwner.setEmail(o.getEmail());
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

//            List<ProjectMember> members = createProjectMembers(savedProject, request.getMembers());

            ProjectMember ownerMember = new ProjectMember();
            ownerMember.setProjectId(savedProject.getId());
            ownerMember.setProjectName(savedProject.getProjectName());
            ownerMember.setUserId(projectOwner.getId());
            ownerMember.setUserName(projectOwner.getUserName());
            ownerMember.setEmail(projectOwner.getEmail());
            ownerMember.setUrlAvatar(projectOwner.getAvatar());
            ownerMember.setRole(ProjectRole.OWNER);
            ownerMember.setCreatedAt(new Date());
            ownerMember.setUpdatedAt(new Date());
            projectMemberRepository.save(ownerMember);


            List<ProjectMemberRequestDto> toInvite = request.getMembers().stream()
                    .filter(m -> !"OWNER".equals(m.getRole()))
                    .collect(Collectors.toList());

            invitationService.createInvitations(
                    savedProject.getId(),
                    savedProject.getProjectCode(),
                    savedProject.getProjectName(),
                    savedProject.getThumbnailUrl(),
                    ownerMember.getUserId(),
                    ownerMember.getUserName(),
                    toInvite
            );

            Factor factor = createFactor(request.getFactor(), savedProject.getId());

            List<Treatment> treatments = createTreatments(savedProject, factor);

            List<Criterion> criterions = createCriterion(savedProject, request.getCriteria());



            // map code treatment to id
            Map<String, String> treatmentCodeToId = treatments.stream()
                    .collect(Collectors.toMap(Treatment::getTreatmentCode, Treatment::getId));

            List<List<String>> layouts = request.getLayout();
            List<List<Plot>> parsedLayout = new ArrayList<>();

            for (List<String> blockData : layouts) {
                List<Plot> plotList = blockData.stream()
                        .map(code -> new Plot(
                                treatmentCodeToId.getOrDefault(code, null),
                                code,
                                null
                        ))
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


    //    private List<ProjectMember> createProjectMembers(Projects project, List<ProjectMemberRequestDto> memberRequests) {
//        List<ProjectMember> membersProject = new ArrayList<>();
//        ProjectMember ownerMember = new ProjectMember();
//
//        if (memberRequests != null) {
//            for (ProjectMemberRequestDto memberRequest : memberRequests) {
//                ProjectMember member = new ProjectMember();
//                member.setProjectId(project.getId());
//                member.setProjectCode(project.getProjectCode());
//                member.setUserName(memberRequest.getName());
//                member.setProjectName(project.getProjectName());
//                member.setUserId(memberRequest.getUserId());
//                member.setRole(ProjectRole.valueOf(memberRequest.getRole()));
//                member.setCreatedAt(new Date());
//                member.setUpdatedAt(new Date());
//                membersProject.add(member);
//            }
//        }
//
//        return projectMemberRepository.saveAll(membersProject);
//    }

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

        Page<Projects> projectPage = projectRepository.findAllByOwner_Id(userDetails.getId(), pageable);
        List<Projects> projects = projectPage.getContent();


        List<String> projectIds = projects.stream()
                .map(Projects::getId)
                .collect(Collectors.toList());


        List<ProjectMember> allMembers = projectMemberRepository.findByProjectIdIn(projectIds);


        Map<String, List<ProjectMember>> memberMap = allMembers.stream()
                .collect(Collectors.groupingBy(ProjectMember::getProjectId));


        List<ProjectResponse> projectResponses = projects.stream()
                .map(project -> {
                    ProjectResponse response = modelMapper.map(project, ProjectResponse.class);

                    List<ProjectMember> projectMembers = memberMap.getOrDefault(project.getId(), Collections.emptyList());

                    List<ProjectMemberResponse> memberResponses = projectMembers.stream()
                            .map(pm -> modelMapper.map(pm, ProjectMemberResponse.class))
                            .collect(Collectors.toList());

                    response.setMembers(memberResponses);
                    return response;
                })
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



    @Override
    public SuccessResponse getProjectsForCurrentUserWithMemberOrGuestRole(UserPagingRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 1. Lấy tất cả membership là MEMBER hoặc GUEST của user
        List<ProjectRole> allowedRoles = List.of(ProjectRole.MEMBER, ProjectRole.GUEST);
        List<ProjectMember> userMemberships = projectMemberRepository.findByUserIdAndRoleIn(userDetails.getId(), allowedRoles);

        List<String> projectIds = userMemberships.stream()
                .map(ProjectMember::getProjectId)
                .distinct()
                .toList();

        if (projectIds.isEmpty()) {
            return new SuccessResponse(Collections.emptyList());
        }

        String sortDir = Optional.ofNullable(request.getSortDir()).orElse("desc");
        String sortBy = Optional.ofNullable(request.getSortBy()).orElse("createdAt");

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );


        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        List<Projects> baseProjects = projectRepository.findByIdIn(projectIds);
        Map<String, Projects> projectInfoMap = baseProjects.stream()
                .collect(Collectors.toMap(Projects::getId, p -> p));

        Page<ProjectDetail> projectPage = projectDetailRepository.findByProjectIdIn(projectIds, pageable);
        List<ProjectDetail> projects = projectPage.getContent();

        // 3. Lấy lại member của các project để set vào ProjectResponse
        List<String> paginatedProjectIds = projects.stream().map(ProjectDetail::getProjectId).toList();
        List<ProjectMember> allMembers = projectMemberRepository.findByProjectIdIn(paginatedProjectIds);

        Map<String, List<ProjectMember>> memberMap = allMembers.stream()
                .collect(Collectors.groupingBy(ProjectMember::getProjectId));

        // 4. Map sang ProjectResponse
        List<ProjectResponse> projectResponses = projects.stream()
                .map(project -> {
                    ProjectResponse response = modelMapper.map(project, ProjectResponse.class);
                    List<ProjectMember> members = memberMap.getOrDefault(project.getProjectId(), Collections.emptyList());
                    Projects base = projectInfoMap.get(project.getProjectId());
                    if (base != null) {
                        response.setStartDate(base.getStartDate());
                        response.setEndDate(base.getEndDate());
                    }
                    response.setId(project.getProjectId());
                    List<ProjectMemberResponse> memberResponses = members.stream()
                            .map(m -> modelMapper.map(m, ProjectMemberResponse.class))
                            .collect(Collectors.toList());
                    response.setMembers(memberResponses);
                    return response;
                })
                .collect(Collectors.toList());

        // 5. Meta
        Pagination meta = new Pagination();
        meta.setPage(projectPage.getNumber());
        meta.setSize(projectPage.getSize());
        meta.setTotalElements(projectPage.getTotalElements());
        meta.setTotalPages(projectPage.getTotalPages());
        meta.setHasNext(projectPage.hasNext());
        meta.setHasPrevious(projectPage.hasPrevious());

        return new SuccessResponse(projectResponses, meta);
    }


    @Override
    public SuccessResponse getPublicProjects(UserPagingRequest request) {
        Sort sort = Sort.by(
                request.getSortDir().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSortBy()
        );

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Projects> projectPage = projectRepository.findByPublicVisibleTrue(pageable);
        List<Projects> projects = projectPage.getContent();

        List<String> projectIds = projects.stream()
                .map(Projects::getId)
                .toList();

        // Lấy danh sách tất cả các member của các project công khai
        List<ProjectMember> allMembers = projectMemberRepository.findByProjectIdIn(projectIds);

        Map<String, List<ProjectMember>> memberMap = allMembers.stream()
                .collect(Collectors.groupingBy(ProjectMember::getProjectId));

        // Map sang ProjectResponse
        List<ProjectResponse> responses = projects.stream()
                .map(project -> {
                    ProjectResponse response = modelMapper.map(project, ProjectResponse.class);

                    List<ProjectMember> members = memberMap.getOrDefault(project.getId(), Collections.emptyList());
                    List<ProjectMemberResponse> memberResponses = members.stream()
                            .map(m -> modelMapper.map(m, ProjectMemberResponse.class))
                            .collect(Collectors.toList());

                    response.setMembers(memberResponses);
                    return response;
                })
                .collect(Collectors.toList());

        // Pagination
        Pagination meta = new Pagination();
        meta.setPage(projectPage.getNumber());
        meta.setSize(projectPage.getSize());
        meta.setTotalElements(projectPage.getTotalElements());
        meta.setTotalPages(projectPage.getTotalPages());
        meta.setHasNext(projectPage.hasNext());
        meta.setHasPrevious(projectPage.hasPrevious());

        return new SuccessResponse(responses, meta);
    }


//    public SuccessResponse searchProjects(String keyword, UserPagingRequest request) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        Pageable pageable = PageRequest.of(
//                request.getPage(),
//                request.getSize(),
//                Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy())
//        );
//
//        Page<Projects> projectPage = projectRepository.findVisibleOrParticipatedProjects(userDetails.getId(), pageable);
//
//        List<Projects> projects = projectPage.getContent();
//        List<String> projectIds = projects.stream().map(Projects::getId).toList();
//
//        List<ProjectMember> userMemberships = projectMemberRepository.findByUserIdAndProjectIdIn(userDetails.getId(), projectIds);
//        Set<String> joinedProjectIds = userMemberships.stream().map(ProjectMember::getProjectId).collect(Collectors.toSet());
//
//        List<ProjectResponse> responses = projects.stream()
//                .filter(p -> keyword == null || p.getProjectName().contains(keyword) || p.getProjectCode().contains(keyword))
//                .map(p -> {
//                    ProjectResponse response = modelMapper.map(p, ProjectResponse.class);
//                    response.setStatus(joinedProjectIds.contains(p.getId()) ? "ĐÃ THAM GIA" : "CÔNG KHAI");
//                    return response;
//                })
//                .toList();
//
//        Pagination meta = new Pagination(
//                projectPage.getNumber(),
//                projectPage.getSize(),
//                projectPage.getTotalElements(),
//                projectPage.getTotalPages(),
//                projectPage.hasNext(),
//                projectPage.hasPrevious()
//        );
//
//        return new SuccessResponse(responses, meta);
//    }




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
        response.setPublicVisible(project.isPublicVisible());
        response.setMembers(members.stream()
                .map(m -> new
                        ProjectMemberResponse(
                                m.getUserId(),
                                m.getUserName(),
                                "",
                                "",
                                m.getEmail(),
                                m.getRole().toString()))
                .collect(Collectors.toList()));
        return response;
    }




    private List<Treatment> createTreatments(Projects project, Factor factor) {
        List<Treatment> treatments = new ArrayList<>();
        if (factor != null && factor.getLevels() != null) {
            for (Level level : factor.getLevels()) {
                Treatment treatment = new Treatment();
                treatment.setProjectId(project.getId());
                treatment.setTreatmentCode(level.getLevelCode());
                treatment.setTreatmentName(level.getLevelName());
                treatment.setLevelId(level.getId());
                treatment.setFactorId(factor.getId());
                treatment.setCreatedAt(new Date());
                treatment.setUpdatedAt(new Date());
                treatments.add(treatment);
            }
        }
        return treatmentRepository.saveAll(treatments);
    }


    private Factor createFactor(FactorRequestDto factorRequest, String projectId) {
            Factor factor = new Factor();
            if (factorRequest!= null) {
                List<Level> levels = new ArrayList<>();
                for (LevelRequestDto levelRequest : factorRequest.getLevels()) {
                    Level level = new Level();
                    level.setId(UUID.randomUUID().toString());
                    level.setLevelCode(levelRequest.getCode());
                    level.setLevelName(levelRequest.getName());
                    levels.add(level);
                }
                factor.setFactorCode(factorRequest.getCode());
                factor.setFactorName(factorRequest.getName());
                factor.setCreatedAt(new Date());
                factor.setUpdatedAt(new Date());
                factor.setLevels(levels);
                factor.setProjectId(projectId);
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



    @Override
    public void updateVisibility(String projectId, boolean isPublic) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(404, 44,
                        "Không tìm thấy project"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi chế độ hiển thị của dự án này");
        }

        logger.info("public" + isPublic);

        project.setPublicVisible(isPublic);
        project.setUpdatedAt(new Date());
        projectRepository.save(project);
    }


    @Override
    public void updateProject(String projectId, UpdateProjectRequest request) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(404, 44, "Không tìm thấy project"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi thông tin của dự án này");
        }

        Optional<Projects> projectWithSameCode = projectRepository.findByProjectCode(request.getProjectCode());
        if (projectWithSameCode.isPresent() && !projectWithSameCode.get().getId().equals(projectId)) {
            throw new AppException(400, 45, "Mã dự án đã được sử dụng cho một dự án khác");
        }

        // update project
        project.setProjectName(request.getProjectName());
        project.setProjectCode(request.getProjectCode());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setThumbnailUrl(request.getThumbnailUrl());
        projectRepository.save(project);

        // update projectdetail
        ProjectDetail detail = projectDetailRepository.findByProjectId(projectId)
                .orElse(new ProjectDetail());
        detail.setProjectId(project.getId());
        detail.setProjectCode(request.getProjectCode());
        detail.setProjectName(request.getProjectName());
        detail.setThumbnailUrl(request.getThumbnailUrl());
        detail.setDescription(request.getDescription());
        projectDetailRepository.save(detail);
    }

    @Override
    public void updateProjectMembers(String projectId, UpdateProjectMembersRequest request) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(404, 44, "Project không tồn tại"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi thành viên");
        }

        // Xử lý cập nhật hoặc xoá
        for (UpdateProjectMembersRequest.MemberUpdateItem item : request.getUpdates()) {
            Optional<ProjectMember> optionalMember = projectMemberRepository.findByProjectIdAndUserId(projectId, item.getUserId());
            if (optionalMember.isPresent()) {
                ProjectMember member = optionalMember.get();
                if (item.isRemove()) {
                    projectMemberRepository.delete(member);
                } else {
                    member.setRole(ProjectRole.valueOf(item.getRole()));
                    member.setUpdatedAt(new Date());
                    projectMemberRepository.save(member);
                }
            }
        }

        logger.info("members" + request.getInvites().toString());

        // Mời thêm thành viên mới
        for (UpdateProjectMembersRequest.MemberInviteItem invite : request.getInvites()) {
            Invitation invitation = new Invitation();
            invitation.setProjectId(project.getId());
            invitation.setInvitedUserId(invite.getUserId());
            invitation.setProjectCode(project.getProjectCode());
            invitation.setProjectName(project.getProjectName());
            invitation.setThumbnailUrlProject(project.getThumbnailUrl());
            invitation.setInviterId(currentUser.getId());
            invitation.setInviterName(currentUser.getUsername());
            invitation.setEmail(invite.getEmail());
            invitation.setInvitedUserName(invite.getName());
            invitation.setRole(invite.getRole());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setCreatedAt(new Date());
            invitation.setUpdatedAt(new Date());
            invitationRepository.save(invitation);

            logger.info("invite" + invitation.toString());

            // Gửi thông báo mời qua WebSocket
            invitationService.sendInvitation(invitation);
        }
    }



    @Override
    public void updateFactorAndCriteria(String projectId, UpdateFactorAndCriterionRequest request) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(404, 44, "Project không tồn tại"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi dữ liệu");
        }

        logger.info("🔄 Update factor and criteria for project: {}", projectId);

        // 1. Cập nhật Factor
        Factor factor = factorRepository.findById(request.getFactorId())
                .orElseThrow(() -> new AppException(404, 44, "Factor không tồn tại"));

        factor.setFactorCode(request.getFactorCode());
        factor.setFactorName(request.getFactorName());
        factor.setUpdatedAt(new Date());

        List<Level> updatedLevels = new ArrayList<>();
        List<Treatment> newTreatments = new ArrayList<>();

        for (UpdateFactorAndCriterionRequest.LevelRequest lr : request.getLevels()) {
            Level level = new Level();
            level.setId((lr.getId() != null && !lr.getId().isBlank()) ? lr.getId() : UUID.randomUUID().toString());
            level.setLevelCode(lr.getLevelCode());
            level.setLevelName(lr.getLevelName());
            updatedLevels.add(level);

            // Tìm hoặc tạo mới Treatment theo levelId
            Treatment treatment = (lr.getId() != null && !lr.getId().isBlank())
                    ? treatmentRepository.findByLevelId(level.getId()).orElse(null)
                    : null;

            if (treatment == null) {
                treatment = new Treatment();
                treatment.setId(UUID.randomUUID().toString());
                treatment.setCreatedAt(new Date());
            }

            treatment.setProjectId(projectId);
            treatment.setFactorId(factor.getId());
            treatment.setLevelId(level.getId());
            treatment.setTreatmentCode(level.getLevelCode());
            treatment.setTreatmentName(level.getLevelName());
            treatment.setUpdatedAt(new Date());

            treatmentRepository.save(treatment);
            newTreatments.add(treatment);
        }

        factor.setLevels(updatedLevels);
        factorRepository.save(factor);

        // 2. Cập nhật các tiêu chí (Criterion)
        List<Criterion> updatedCriteria = new ArrayList<>();
        for (CriterionRequest cr : request.getCriteria()) {
            Criterion criterion;

            if (cr.getId() != null && !cr.getId().isBlank()) {
                criterion = criterionRepository.findById(cr.getId())
                        .orElseThrow(() -> new AppException(404, 44, "Tiêu chí không tồn tại: " + cr.getId()));
            } else {
                criterion = new Criterion();
                criterion.setId(UUID.randomUUID().toString());
                criterion.setProjectId(projectId);
                criterion.setCreatedAt(new Date());
            }

            criterion.setCriterionCode(cr.getCriterionCode());
            criterion.setCriterionName(cr.getCriterionName());
            criterion.setUpdatedAt(new Date());

            criterionRepository.save(criterion);
            updatedCriteria.add(criterion);
        }

        // 3. Cập nhật ProjectDetail
        ProjectDetail detail = projectDetailRepository.findByProjectId(projectId)
                .orElseThrow(() -> new AppException(404, 44, "ProjectDetail không tồn tại"));

        detail.setFactor(factor);
        detail.setCriterionList(updatedCriteria);
        detail.setTreatmentList(newTreatments);
        projectDetailRepository.save(detail);

        // 4. Cập nhật Layout
        Experiment experiment = layoutArrangementRepository.findByProjectId(projectId)
                .orElseThrow(() -> new AppException(404, 44, "Experiment không tồn tại"));

        experiment.setTreatments(newTreatments);
        experiment.setUpdatedAt(new Date());
        experiment.setType(request.getExperimentType());
        experiment.setBlocks(request.getBlocks());
        experiment.setReplicates(request.getReplicates());
        experiment.setColumns(request.getColumns());
        experiment.setLayout(request.getLayout());
        experiment.setUpdatedAt(new Date());

        layoutArrangementRepository.save(experiment);
        logger.info("✅ Cập nhật yếu tố và chỉ tiêu thành công cho project {}", projectId);
    }



    @Override
    public void deleteProjectById(String projectId) {

        projectRepository.deleteById(projectId);


        projectDetailRepository.deleteByProjectId(projectId);
        projectMemberRepository.deleteByProjectId(projectId);
        factorRepository.deleteByProjectId(projectId);
        criterionRepository.deleteByProjectId(projectId);
        layoutArrangementRepository.deleteByProjectId(projectId);
        measurementRepository.deleteByProjectId(projectId);
        List<Measurement> measurements = measurementRepository.findByProjectId(projectId);
        List<String> measurementIds = measurements.stream()
                .map(Measurement::getId)
                .collect(Collectors.toList());
        measurementHistoryRepository.deleteByMeasurementIdIn(measurementIds);

        invitationRepository.deleteByProjectId(projectId);
    }




















}

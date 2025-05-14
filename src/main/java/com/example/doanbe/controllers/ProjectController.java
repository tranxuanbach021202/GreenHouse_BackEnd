package com.example.doanbe.controllers;


import com.example.doanbe.dto.request.ProjectPublicRequest;
import com.example.doanbe.dto.request.UpdateFactorAndCriterionRequest;
import com.example.doanbe.dto.request.UpdateProjectMembersRequest;
import com.example.doanbe.dto.request.UpdateProjectRequest;
import com.example.doanbe.payload.request.CreateProjectRequest;
import com.example.doanbe.payload.request.QrGenerateRequest;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.ProjectDetailResponse;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.Impl.EmailServiceImpl;
import com.example.doanbe.services.ProjectService;
import com.example.doanbe.services.QrService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;


@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> createProject(
            @RequestBody @Valid CreateProjectRequest request
    ) {

        return ResponseEntity.ok(projectService.createProject(request));
    }

    @GetMapping("/list")
    public  ResponseEntity<SuccessResponse> listProjects(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        UserPagingRequest request = UserPagingRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir).build();

        return ResponseEntity.ok(projectService.getProjectsForCurrentUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getProjectById(@PathVariable String id) {
        ProjectDetailResponse response = projectService.getProjectDetailById(id);
        return ResponseEntity.ok(new SuccessResponse(response));
    }

    @Autowired
    private QrService qrCodeService;


    @PostMapping("/generate/qr")
    public ResponseEntity<byte[]> generateQrPdf(@RequestBody QrGenerateRequest request) {
        try {
            byte[] pdfBytes = qrCodeService.generateQrPdf(request.getProjectId(), request.getUrlApp());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("qr-codes.pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/{projectId}/visibility")
    public ResponseEntity<?> updateProjectVisibility(
            @PathVariable String projectId,
            @RequestBody ProjectPublicRequest request
    ) {

        logger.info("Update project visibility: " + request.isPublicVisible());

        projectService.updateVisibility(projectId, request.isPublicVisible());
        String message = request.isPublicVisible() ? "Dự án đã được public." : "Dự án đã được đặt về chế độ riêng tư.";
        return ResponseEntity.ok(
                new MessageResponse(message)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable("id") String projectId,
            @RequestBody UpdateProjectRequest request) {

        projectService.updateProject(projectId, request);
        return ResponseEntity.ok(new MessageResponse("Cập nhật thông tin dự án thành công"));
    }

    @PutMapping("/{projectId}/members")
    public ResponseEntity<?> updateProjectMembers(
            @PathVariable String projectId,
            @RequestBody UpdateProjectMembersRequest request
    ) {
        projectService.updateProjectMembers(projectId, request);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{projectId}/factor-and-criteria")
    public ResponseEntity<?> updateFactorAndCriteria(
            @PathVariable String projectId,
            @RequestBody UpdateFactorAndCriterionRequest request
    ) {
        projectService.updateFactorAndCriteria(projectId, request);
        return ResponseEntity.ok(new MessageResponse("Cập nhật thành công yếu tố và chỉ tiêu."));
    }





    @GetMapping("/public")
    public ResponseEntity<SuccessResponse> getPublicProjects(@ModelAttribute UserPagingRequest request) {
        return ResponseEntity.ok(projectService.getPublicProjects(request));
    }


    @GetMapping("/member-or-guest")
    public ResponseEntity<SuccessResponse> getProjectsAsMemberOrGuest(@ModelAttribute UserPagingRequest request) {
        return ResponseEntity.ok(projectService.getProjectsForCurrentUserWithMemberOrGuestRole(request));
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
        try {
            projectService.deleteProjectById(projectId);
            return ResponseEntity.ok("Xoá dự án và dữ liệu liên quan thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xoá thất bại: " + e.getMessage());
        }
    }







}

package com.example.doanbe.controllers;


import com.example.doanbe.payload.request.CreateProjectRequest;
import com.example.doanbe.payload.request.QrGenerateRequest;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.ProjectDetailResponse;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.ProjectService;
import com.example.doanbe.services.QrService;
import jakarta.validation.Valid;
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
}

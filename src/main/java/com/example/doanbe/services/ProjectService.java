package com.example.doanbe.services;

import com.example.doanbe.payload.request.CreateProjectRequest;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.ProjectDetailResponse;
import com.example.doanbe.payload.response.SuccessResponse;

public interface ProjectService {
    MessageResponse createProject(CreateProjectRequest createProjectRequest);

    SuccessResponse getProjectsForCurrentUser(UserPagingRequest request);

    ProjectDetailResponse getProjectDetailById(String projectId);
}

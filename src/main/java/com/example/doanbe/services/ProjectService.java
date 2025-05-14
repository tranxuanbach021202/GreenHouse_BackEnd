package com.example.doanbe.services;

import com.example.doanbe.dto.request.UpdateFactorAndCriterionRequest;
import com.example.doanbe.dto.request.UpdateProjectMembersRequest;
import com.example.doanbe.dto.request.UpdateProjectRequest;
import com.example.doanbe.payload.request.CreateProjectRequest;
import com.example.doanbe.payload.request.UserPagingRequest;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.ProjectDetailResponse;
import com.example.doanbe.payload.response.SuccessResponse;

public interface ProjectService {
    MessageResponse createProject(CreateProjectRequest createProjectRequest);

    SuccessResponse getProjectsForCurrentUser(UserPagingRequest request);

    SuccessResponse getProjectsForCurrentUserWithMemberOrGuestRole(UserPagingRequest request);

    SuccessResponse getPublicProjects(UserPagingRequest request);

    ProjectDetailResponse getProjectDetailById(String projectId);

    void updateVisibility(String projectId, boolean isPublic);

    void updateProject(String projectId, UpdateProjectRequest request);

    void updateProjectMembers(String projectId, UpdateProjectMembersRequest request);

    void updateFactorAndCriteria(String projectId, UpdateFactorAndCriterionRequest request);

    void deleteProjectById(String projectId);
}

package com.example.doanbe.repository;

import com.example.doanbe.document.ProjectMember;
import com.example.doanbe.document.User;
import com.example.doanbe.enums.InvitationStatus;
import com.example.doanbe.enums.ProjectRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends MongoRepository<ProjectMember, String> {
    List<ProjectMember> findByProjectId(String projectId);
    List<ProjectMember> findByProjectIdIn(List<String> projectIds);
    Optional<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);
    List<ProjectMember> findByUserIdAndRoleIn(String userId, List<ProjectRole> roles);
    void deleteByProjectId(String projectId);
    List<ProjectMember> findByUserId(String userId);
    
}

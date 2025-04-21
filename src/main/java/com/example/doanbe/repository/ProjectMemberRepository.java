package com.example.doanbe.repository;

import com.example.doanbe.document.ProjectMember;
import com.example.doanbe.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectMemberRepository extends MongoRepository<ProjectMember, String> {
    List<ProjectMember> findByProjectId(String projectId);
}

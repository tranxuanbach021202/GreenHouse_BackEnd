package com.example.doanbe.repository;

import com.example.doanbe.document.Invitation;
import com.example.doanbe.enums.InvitationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvitationRepository extends MongoRepository<Invitation, String> {
    List<Invitation> findByInvitedUserIdAndStatus(String invitedUserId, InvitationStatus status);
    void deleteByProjectId(String projectId);
}

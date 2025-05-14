package com.example.doanbe.services;

import com.example.doanbe.document.Invitation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvitationService {

    void sendInvitation(Invitation message);

    List<Invitation> getInvitationsForUser();

    @Transactional
    void acceptInvitation(String invitationId);

    @Transactional
    void rejectInvitation(String invitationId);
}

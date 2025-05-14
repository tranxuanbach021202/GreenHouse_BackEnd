package com.example.doanbe.services.Impl;


import com.example.doanbe.document.Invitation;
import com.example.doanbe.document.ProjectMember;
import com.example.doanbe.document.User;
import com.example.doanbe.dto.ProjectMemberRequestDto;
import com.example.doanbe.enums.InvitationStatus;
import com.example.doanbe.enums.ProjectRole;
import com.example.doanbe.repository.InvitationRepository;
import com.example.doanbe.repository.ProjectMemberRepository;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.services.InvitationService;
import com.example.doanbe.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository memberRepository;

    @Autowired
    private InvitationRepository invitationRepository;



    public void createInvitations(
            String projectId,
            String projectCode,
            String projectName,
            String thumbnailUrlProject,
            String inviterId,
            String inviterName,
            List<ProjectMemberRequestDto> toInvite
    ) {
        Date now = new Date();
        for (ProjectMemberRequestDto dto : toInvite) {
            Invitation inv = new Invitation();
            inv.setProjectId(projectId);
            inv.setProjectCode(projectCode);
            inv.setProjectName(projectName);
            inv.setThumbnailUrlProject(thumbnailUrlProject);
            inv.setInviterId(inviterId);
            inv.setInviterName(inviterName);
            inv.setInvitedUserId(dto.getUserId());
            inv.setInvitedUserName(dto.getName());
            inv.setEmail(dto.getEmail());
            inv.setRole(dto.getRole());
            inv.setStatus(InvitationStatus.PENDING);
            inv.setCreatedAt(now);
            inv.setUpdatedAt(now);
            invitationRepository.save(inv);
            sendInvitation(inv);
        }
    }

    @Override
    public void sendInvitation(Invitation message) {

        // gửi qua WebSocket
        messagingTemplate.convertAndSend(
                "/topic/invitations/" + message.getInvitedUserId(),
                message
        );
    }


    @Override
    public List<Invitation> getInvitationsForUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return invitationRepository.findByInvitedUserIdAndStatus(userDetails.getId(), InvitationStatus.PENDING);
    }


    @Transactional
    @Override
    public void acceptInvitation(String invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        // Đảm bảo người dùng hiện tại là người được mời
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!invitation.getInvitedUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Không có quyền thực hiện hành động này");
        }


        User user = userRepository.findById(currentUser.getId()).get();

        // Cập nhật status invitation
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setUpdatedAt(new Date());
        invitationRepository.save(invitation);

        // Thêm vào ProjectMember
        ProjectMember member = new ProjectMember();
        member.setProjectId(invitation.getProjectId());
        member.setProjectCode(invitation.getProjectCode());
        member.setProjectName(invitation.getProjectName());
        member.setUserId(invitation.getInvitedUserId());
        member.setUserName(invitation.getInvitedUserName());
        member.setEmail(invitation.getEmail());
        member.setUrlAvatar(user.getUrlAvatar());
        member.setRole(ProjectRole.valueOf(invitation.getRole()));
        member.setCreatedAt(new Date());
        member.setUpdatedAt(new Date());
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void rejectInvitation(String invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!invitation.getInvitedUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Không có quyền thực hiện hành động này");
        }

        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setUpdatedAt(new Date());
        invitationRepository.save(invitation);
    }



}

package com.example.doanbe.services.Impl;


import com.example.doanbe.document.Invitation;
import com.example.doanbe.services.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendInvitation(Invitation message) {
        // gửi qua WebSocket
        messagingTemplate.convertAndSend(
                "/topic/invitations/" + message.getInvitedUserId(),
                message
        );

        // Nếu bạn lưu lời mời vào DB thì thêm ở đây
        // invitationRepository.save(...)
    }
}

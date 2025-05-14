package com.example.doanbe.controllers;

import com.example.doanbe.document.Invitation;
import com.example.doanbe.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public ResponseEntity<List<Invitation>> getMyInvitations() {
        List<Invitation> invitations = invitationService.getInvitationsForUser();
        return ResponseEntity.ok(invitations);
    }


    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable String id) {
        try {
            invitationService.acceptInvitation(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lời mời đã được chấp nhận"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectInvitation(@PathVariable String id) {
        try {
            invitationService.rejectInvitation(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lời mời đã bị từ chối"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

}

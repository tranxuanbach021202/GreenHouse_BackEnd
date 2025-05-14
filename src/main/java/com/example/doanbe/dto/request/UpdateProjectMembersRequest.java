package com.example.doanbe.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProjectMembersRequest {
    private List<MemberUpdateItem> updates;
    private List<MemberInviteItem> invites;

    @Data
    public static class MemberUpdateItem {
        private String userId;
        private String role;
        private boolean remove;
    }

    @Data
    public static class MemberInviteItem {
        private String userId;
        private String email;
        private String name;
        private String role;
    }
}

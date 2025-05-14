package com.example.doanbe.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String urlAvatar;
    private String bio;

    public UserProfileResponse(String id, String username, String email, String displayName, String urlAvatar, String bio) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.urlAvatar = urlAvatar;
        this.bio = bio;
    }
}

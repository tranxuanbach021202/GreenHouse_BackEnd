package com.example.doanbe.document;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    private String projectId;
    private String projectName;
    private String inviterName;
    private String invitedUserId;
}

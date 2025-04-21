package com.example.doanbe.document;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ProjectOwner {
    private String id;
    private String userName;
    private String displayName;
    private String avatar;
}
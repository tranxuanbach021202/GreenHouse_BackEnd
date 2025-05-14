package com.example.doanbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProjectPublicRequest {
    @JsonProperty("isPublic")
    private boolean publicVisible;
}


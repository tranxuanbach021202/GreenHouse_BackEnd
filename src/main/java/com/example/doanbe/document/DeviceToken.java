package com.example.doanbe.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "device_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceToken {
    @Id
    private String id;

    private String userId;
    private String token;
    private String platform;
    private LocalDateTime updatedAt;
}

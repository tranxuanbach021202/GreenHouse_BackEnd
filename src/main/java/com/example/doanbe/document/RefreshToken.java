package com.example.doanbe.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;

    @DBRef
    private User user;

    private String token;
    private Instant expiryDate;
}

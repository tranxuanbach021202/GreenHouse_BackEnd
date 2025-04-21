package com.example.doanbe.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String oneTimePassword;
    private boolean isVerified = false;
    private LocalDateTime otpExpiryTime;
    private Integer otpAttempts = 0;

    @DBRef
    private Set<Role> roles;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setOtp(String otp) {
        this.oneTimePassword = otp;
        this.otpExpiryTime = LocalDateTime.now().plusMinutes(5);
    }

    public boolean isOtpValid(String otp) {
        return otp.equals(this.oneTimePassword) && LocalDateTime.now().isBefore(this.otpExpiryTime);
    }
}

package com.example.doanbe.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class SignUpRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 20, message = "Username phải từ 3-20 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private Set<String> roles;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 40, message = "Password phải từ 6-40 ký tự")
    private String password;


}

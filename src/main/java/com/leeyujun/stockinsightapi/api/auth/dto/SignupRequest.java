package com.leeyujun.stockinsightapi.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class SignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    public SignupRequest() {}

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getNickname() {return nickname;}
}

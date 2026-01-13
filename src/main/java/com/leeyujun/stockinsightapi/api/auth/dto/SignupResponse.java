package com.leeyujun.stockinsightapi.api.auth.dto;

public class SignupResponse {
    private final Long userId;
    private final String email;
    private final String nickname;


    public SignupResponse(Long userId, String email, String nickname) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }

    public Long getUserId() {return userId;}
    public String getEmail() {return email;}
    public String getNickname() {return nickname;}
}

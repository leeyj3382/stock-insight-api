package com.leeyujun.stockinsightapi.api.post.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest (
    @NotBlank String title,
    @NotBlank String content
){}

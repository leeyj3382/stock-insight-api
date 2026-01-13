package com.leeyujun.stockinsightapi.api.post.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest (
    @NotBlank String title,
    @NotBlank String content

    ){}

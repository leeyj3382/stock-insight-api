package com.leeyujun.stockinsightapi.api.post.dto;

import java.time.Instant;

public record PostListItemResponse (
        Long id,
        String title,
        Instant createdAt,
        Instant updatedAt
){}



package com.leeyujun.stockinsightapi.api.post.dto;

import java.time.Instant;

public record PostResponse (
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt,
        boolean isOwner
){}

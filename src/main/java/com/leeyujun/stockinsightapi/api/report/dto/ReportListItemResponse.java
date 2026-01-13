package com.leeyujun.stockinsightapi.api.report.dto;

import java.time.Instant;

public record ReportListItemResponse (
        Long id,
        String ticker,
        String market,
        String summary,
        Instant createdAt
){}

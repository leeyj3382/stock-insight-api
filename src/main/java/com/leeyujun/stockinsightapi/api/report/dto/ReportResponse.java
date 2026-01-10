package com.leeyujun.stockinsightapi.api.report.dto;


import java.time.Instant;

public record ReportResponse(
        Long id,
        String ticker,
        String market,
        String summary,
        String inputJson,
        String sourcesJson,
        String resultJson,
        Instant createdAt
) { }

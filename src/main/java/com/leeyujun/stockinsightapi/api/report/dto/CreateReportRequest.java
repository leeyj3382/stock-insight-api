package com.leeyujun.stockinsightapi.api.report.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReportRequest(
        @NotBlank String ticker,
        String market,
        @NotBlank String summary,
        @NotBlank String inputJson,
        @NotBlank String sourcesJson,
        @NotBlank String resultJson
) {}

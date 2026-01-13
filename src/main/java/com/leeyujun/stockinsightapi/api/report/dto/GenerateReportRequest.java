package com.leeyujun.stockinsightapi.api.report.dto;

import jakarta.validation.constraints.NotBlank;

public record GenerateReportRequest(
        @NotBlank String ticker,
        String market
) {}

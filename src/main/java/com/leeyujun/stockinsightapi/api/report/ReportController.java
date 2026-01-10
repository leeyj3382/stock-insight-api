package com.leeyujun.stockinsightapi.api.report;

import com.leeyujun.stockinsightapi.api.report.dto.CreateReportRequest;
import com.leeyujun.stockinsightapi.api.report.dto.ReportListItemResponse;
import com.leeyujun.stockinsightapi.api.report.dto.ReportResponse;
import com.leeyujun.stockinsightapi.common.security.AuthUtil;
import com.leeyujun.stockinsightapi.domain.report.entity.Report;
import com.leeyujun.stockinsightapi.domain.report.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ReportResponse create(@Valid @RequestBody CreateReportRequest req) {
        Long userId = AuthUtil.currentUserId();
        Report r = reportService.create(userId, req);
        return toResponse(r);
    }

    @GetMapping
    public List<ReportListItemResponse> listMine() {
        Long userId = AuthUtil.currentUserId();
        return reportService.listMine(userId).stream()
                .map(r -> new ReportListItemResponse(r.getId(), r.getTicker(), r.getMarket(), r.getSummary(), r.getCreatedAt()))
                .toList();
    }

    @GetMapping("/{id}")
    public ReportResponse getMine(@PathVariable Long id) {
        Long userId = AuthUtil.currentUserId();
        Report r = reportService.getMine(userId, id);
        return toResponse(r);
    }

    @DeleteMapping("/{id}")
    public void deleteMine(@PathVariable Long id) {
        Long userId = AuthUtil.currentUserId();
        reportService.deleteMine(userId, id);
    }

    private ReportResponse toResponse(Report r) {
        return new ReportResponse(
                r.getId(),
                r.getTicker(),
                r.getMarket(),
                r.getSummary(),
                r.getInputJson(),
                r.getSourcesJson(),
                r.getResultJson(),
                r.getCreatedAt()
        );
    }
}

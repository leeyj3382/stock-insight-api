package com.leeyujun.stockinsightapi.domain.report.service;


import com.leeyujun.stockinsightapi.api.report.dto.CreateReportRequest;
import com.leeyujun.stockinsightapi.domain.report.entity.Report;
import com.leeyujun.stockinsightapi.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public Report create(Long userId, CreateReportRequest req) {
        Report r = new Report();
        r.setUserId(userId);
        r.setTicker(req.ticker());
        r.setMarket(req.market()==null || req.market().isBlank() ? "US" : req.market());
        r.setSummary(req.summary());
        r.setInputJson(req.inputJson());
        r.setSourcesJson(req.sourcesJson());
        r.setResultJson(req.resultJson());

        return reportRepository.save(r);

    }

    @Transactional(readOnly = true)
    public List<Report> listMine(Long userId){
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Report getMine(Long userId, Long reportId){
        return reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Transactional
    public void deleteMine(Long userId, Long reportId){
        reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.deleteByIdAndUserId(reportId, userId);
    }
}

package com.leeyujun.stockinsightapi.domain.report.service;


import com.leeyujun.stockinsightapi.api.report.dto.CreateReportRequest;
import com.leeyujun.stockinsightapi.domain.report.entity.Report;
import com.leeyujun.stockinsightapi.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leeyujun.stockinsightapi.api.report.dto.GenerateReportRequest;
import com.leeyujun.stockinsightapi.common.ai.OpenAiClient;

import java.util.List;


@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper om;

    public ReportService(ReportRepository reportRepository, OpenAiClient openAiClient, ObjectMapper om) {
        this.reportRepository = reportRepository;
        this.openAiClient = openAiClient;
        this.om = om;
    }


    @Transactional
    public Report generateWithGpt(Long userId, GenerateReportRequest req) {
        String ticker = req.ticker().trim();
        String market = (req.market() == null || req.market().isBlank()) ? "US" : req.market().trim();

        String reportText = openAiClient.generateStockReport(ticker, market);

        String summary = firstNonBlankLine(reportText);
        if (summary.length() > 120) summary = summary.substring(0, 120);


        Report r = new Report();
        r.setUserId(userId);
        r.setTicker(ticker);
        r.setMarket(market);
        r.setSummary(summary.isBlank() ? (ticker + " 리포트") : summary);

        try {
            r.setInputJson(om.writeValueAsString(new InputPayload(ticker, market)));
            r.setSourcesJson("[]");
            r.setResultJson(om.writeValueAsString(new ResultPayload(reportText)));
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        return reportRepository.save(r);
    }

    private String firstNonBlankLine(String s) {
        if (s == null) return "";
        for (String line : s.split("\n")) {
            String t = line.strip();
            if (!t.isBlank()) return t.replaceAll("^[-*•]+\\s*", ""); // bullet 제거
        }
        return "";
    }

    private record InputPayload(String ticker, String market) {}
    private record ResultPayload(String reportText) {}


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

package com.leeyujun.stockinsightapi.domain.report.entity;

import jakarta.persistence.*;
import java.time.Instant;


@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_user_created", columnList = "user_id, created_at")
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String ticker;

    @Column(nullable = false, length = 10)
    private String market = "US";

    @Column(nullable = false, length = 255)
    private String summary;

    @Lob
    @Column(name = "input_json", nullable = false)
    private String inputJson;

    @Lob
    @Column(name = "sources_json", nullable = false)
    private String sourcesJson;

    @Lob
    @Column(name = "result_json", nullable = false)
    private String resultJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {return id;}
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}
    public String getTicker() {return ticker;}
    public void setTicker(String ticker) {this.ticker = ticker;}
    public String getMarket() {return market;}
    public void setMarket(String market) {this.market = market;}
    public String getSummary() {return summary;}
    public void setSummary(String summary) {this.summary = summary;}
    public String getInputJson() {return inputJson;}
    public void setInputJson(String inputJson) {this.inputJson = inputJson;}
    public String getSourcesJson() {return sourcesJson;}
    public void setSourcesJson(String sourcesJson) {this.sourcesJson = sourcesJson;}
    public String getResultJson() {return resultJson;}
    public void setResultJson(String resultJson) {this.resultJson = resultJson;}
    public Instant getCreatedAt() {return createdAt;}
}

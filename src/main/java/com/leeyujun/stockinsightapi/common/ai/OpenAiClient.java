package com.leeyujun.stockinsightapi.common.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final OpenAiProperties props;
    private final ObjectMapper om;

    public OpenAiClient(RestClient.Builder builder, OpenAiProperties props, ObjectMapper om) {
        this.props = props;
        this.om = om;

        System.out.println("OPENAI KEY startsWith sk- ? " + (props.apiKey()!=null && props.apiKey().startsWith("sk-")));


        // Spring recommends RestClient for most traditional use cases. :contentReference[oaicite:4]{index=4}
        this.restClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.apiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .requestFactory(factory -> factory.setConnectTimeout(Duration.ofSeconds(10)))
//                .requestFactory(factory -> factory.setReadTimeout(Duration.ofSeconds(60)))
                .build();
    }

    public String generateStockReport(String ticker, String market) {
        // Responses API: POST /v1/responses :contentReference[oaicite:5]{index=5}
        var prompt = """
                You are a financial analyst. Write a short, practical stock summary report in Korean.
                Constraints:
                - Not investment advice. Add a one-line disclaimer at the end.
                - Keep it concise: 8~14 bullet points max.
                - Sections: (1) 회사/티커 요약 (2) 최근 이슈/모멘텀 (3) 리스크 (4) 체크할 지표/뉴스
                Input:
                - ticker: %s
                - market: %s
                """.formatted(ticker, market);

        try {
            String body = """
                    {
                      "model": "%s",
                      "input": [
                        {
                          "role": "user",
                          "content": %s
                        }
                      ],
                      "store": false
                    }
                    """.formatted(props.model(), om.writeValueAsString(prompt));

            String raw = restClient.post()
                    .uri("/responses")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return extractOutputText(raw);

        } catch (Exception e) {
            throw new RuntimeException("OpenAI 호출 실패: " + e.getMessage(), e);
        }
    }



    private String extractOutputText(String rawJson) throws Exception {
        JsonNode root = om.readTree(rawJson);

        // 일부 응답에 output_text 유사 필드가 있을 수 있으나, 안전하게 output를 스캔
        JsonNode output = root.path("output");
        if (!output.isArray()) return "";

        StringBuilder sb = new StringBuilder();
        for (JsonNode item : output) {
            // message 타입인 경우 content 안에서 text 찾기
            JsonNode content = item.path("content");
            if (content.isArray()) {
                for (JsonNode c : content) {
                    String type = c.path("type").asText("");
                    if ("output_text".equals(type) || "text".equals(type)) {
                        String text = c.path("text").asText("");
                        if (!text.isBlank()) {
                            if (!sb.isEmpty()) sb.append("\n");
                            sb.append(text);
                        }
                    }
                }
            }
        }
        return sb.toString().trim();
    }
}

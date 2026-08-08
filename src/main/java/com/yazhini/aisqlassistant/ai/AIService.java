package com.yazhini.aisqlassistant.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AIService {

    private final WebClient webClient;

    @Value("${openrouter.api.key}")
    private String apiKey;

    public AIService(WebClient webClient) {
        this.webClient = webClient;
    }

    // ===========================
    // Generate SQL
    // ===========================
    public String generateSQL(String question) {

        String prompt = """
                You are an expert PostgreSQL SQL generator.

                Database Schema:

                Table: employees
                --------------------------------
                id          SERIAL PRIMARY KEY
                name        VARCHAR(100)
                department  VARCHAR(100)
                salary      INT

                Rules:
                - Generate ONLY PostgreSQL SQL.
                - Use ONLY the employees table.
                - Return ONLY the SQL query.
                - Never explain anything.
                - Never use markdown.

                User Question:
                %s
                """.formatted(question);

        String body = """
                {
                  "model": "google/gemini-3.5-flash-lite",
                  "max_tokens": 500,
                  "temperature": 0,
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(
                prompt
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
        );

        String response = webClient.post()
                .uri("https://openrouter.ai/api/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("HTTP-Referer", "http://localhost:8080")
                .header("X-Title", "AI SQL Assistant")
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        error -> error.bodyToMono(String.class)
                                .map(RuntimeException::new)
                )
                .bodyToMono(String.class)
                .block();

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String sql = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            sql = sql.replace("```sql", "")
                    .replace("```", "")
                    .trim();

            return sql;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===========================
    // Explain Database Result
    // ===========================
    public String explainResult(String question, Object result) {

        String prompt = """
                You are a helpful AI assistant.

                The user asked:
                %s

                The database returned:
                %s

                Give a short, natural language answer.
                Do not mention SQL.
                """.formatted(question, result.toString());

        String body = """
                {
                  "model": "google/gemini-3.5-flash-lite",
                  "max_tokens": 300,
                  "temperature": 0.3,
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(
                prompt
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
        );

        String response = webClient.post()
                .uri("https://openrouter.ai/api/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("HTTP-Referer", "http://localhost:8080")
                .header("X-Title", "AI SQL Assistant")
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        error -> error.bodyToMono(String.class)
                                .map(RuntimeException::new)
                )
                .bodyToMono(String.class)
                .block();

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .replace("```", "")
                    .trim();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package com.dsar.service;

import com.dsar.domain.DsarRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
* Optional stretch feature: drafts a first-pass response letter to the data
  * subject using an LLM API. Disabled by default (dsar.letter-draft.enabled)
  * so the core application has zero hard dependency on any external AI
  * provider or API key. When enabled, this calls Anthropic's Messages API.
  */
@Service
  @Slf4j
  public class LetterDraftService {

@Value("${dsar.letter-draft.enabled}")
    private boolean enabled;

@Value("${dsar.letter-draft.api-key}")
    private String apiKey;

@Value("${dsar.letter-draft.model}")
    private String model;

private final RestClient restClient = RestClient.builder()
    .baseUrl("https://api.anthropic.com/v1")
    .build();

public String draftLetter(DsarRequest request) {
  if (!enabled || apiKey == null || apiKey.isBlank()) {
    return "Letter drafting is disabled. Set dsar.letter-draft.enabled=true and provide an API key to enable it.";
  }

    String prompt = "Write a short, formal GDPR subject access response letter to "
      + request.getSubjectName() + " (" + request.getSubjectEmail() + ") "
      + "regarding their " + request.getType() + " request, reference "
      + request.getReferenceCode() + ". Keep it concise and professional.";

    try {
      Map<String, Object> body = Map.of(
        "model", model,
        "max_tokens", 600,
        "messages", new Object[] {
          Map.of("role", "user", "content", prompt)
        }
        );

  Map<?, ?> response = restClient.post()
    .uri("/messages")
    .header("x-api-key", apiKey)
    .header("anthropic-version", "2023-06-01")
    .contentType(MediaType.APPLICATION_JSON)
    .body(body)
    .retrieve()
    .body(Map.class);

  return extractText(response);
    } catch (Exception e) {
      log.warn("Letter draft generation failed: {}", e.getMessage());
      return "Automatic draft unavailable; please draft the response manually.";
    }
}

@SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> response) {
      if (response == null) {
        return "No response from LLM provider.";
      }
      Object content = response.get("content");
      if (content instanceof Iterable<?> items) {
        StringBuilder sb = new StringBuilder();
        for (Object item : items) {
          if (item instanceof Map<?, ?> map && map.get("text") != null) {
            sb.append(map.get("text"));
          }
        }
        return sb.toString();
      }
      return String.valueOf(content);
    }
  }

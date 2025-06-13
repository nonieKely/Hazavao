package com.example.demo.endpoint.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
public class RequestController {
    // new key provided by Tanjona because the old one is not available anymore
    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/hazavao")
    public String hazavao(@RequestParam String teny) {
        String url = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", "Hazavao amin'ny teny malagasy ny teny hoe : " + teny
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<?, ?> json = response.getBody();
        if (json == null) return "Erreur: réponse vide";

        List<?> choices = (List<?>) json.get("choices");
        if (choices.isEmpty()) return "Erreur: pas de choix dans la réponse";

        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> messageResponse = (Map<?, ?>) choice.get("message");

        return (String) messageResponse.get("content");
    }
}

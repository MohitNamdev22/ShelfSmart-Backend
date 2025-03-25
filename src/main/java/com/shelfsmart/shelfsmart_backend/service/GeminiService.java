package com.shelfsmart.shelfsmart_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, List<Map<String, String>>> generateSuggestions(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + prompt + "\"}]}]}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode candidates = rootNode.path("candidates");
            if (!candidates.isArray() || candidates.size() == 0) {
                System.out.println("No candidates in Gemini response: " + response.getBody());
                return Map.of("error", List.of(Map.of("message", "No suggestions generated")));
            }

            String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            System.out.println("Gemini Raw Response: " + rawText);
            return parseSuggestions(rawText);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", List.of(Map.of("message", "Error fetching AI suggestions: " + e.getMessage())));
        }
    }

    private Map<String, List<Map<String, String>>> parseSuggestions(String rawText) {
        Map<String, List<Map<String, String>>> suggestions = new HashMap<>();
        suggestions.put("High Urgency", new ArrayList<>());
        suggestions.put("Medium Urgency", new ArrayList<>());
        suggestions.put("Low Urgency", new ArrayList<>());
        suggestions.put("Considerations", new ArrayList<>());

        String[] lines = rawText.split("\n");
        String currentCategory = null;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("**High Priority (Urgent)") || line.contains("High Priority (Urgent)")) {
                currentCategory = "High Urgency";
            } else if (line.startsWith("**Medium Priority") || line.contains("Medium Priority")) {
                currentCategory = "Medium Urgency";
            } else if (line.startsWith("**Low Priority") || line.contains("Low Priority")) {
                currentCategory = "Low Urgency";
            } else if (line.startsWith("**Important Notes") || line.contains("Important Notes")) {
                currentCategory = "Considerations";
            } else if (line.startsWith("* **") && currentCategory != null && !currentCategory.equals("Considerations")) {
                Pattern itemPattern = Pattern.compile("\\* \\*\\*([^:]+):\\*\\*\\s*(.*)");
                Matcher matcher = itemPattern.matcher(line);
                if (matcher.find()) {
                    String item = matcher.group(1).trim();
                    String desc = matcher.group(2).trim();
                    suggestions.get(currentCategory).add(Map.of("item", item, "description", desc));
                }
            } else if (line.startsWith("*") && "Considerations".equals(currentCategory)) {
                suggestions.get("Considerations").add(Map.of("note", line.substring(1).trim()));
            }
        }

        System.out.println("Parsed Suggestions: " + suggestions);
        return suggestions;
    }
}
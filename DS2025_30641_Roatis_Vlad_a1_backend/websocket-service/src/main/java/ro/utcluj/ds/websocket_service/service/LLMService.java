package ro.utcluj.ds.websocket_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private static final String MODEL_NAME = "gemini-2.5-flash";

    public LLMService() {
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public String generateResponse(String userMessage) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "⚠️ API Key neconfigurat.";
        }

        try {
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" 
                          + MODEL_NAME 
                          + ":generateContent?key=" + apiKey;

            Map<String, String> part = new HashMap<>();
            part.put("text", "Ești un asistent inteligent pentru o aplicație de monitorizare a energiei. " +
                             "Răspunde scurt, la obiect și politicos în limba română la următoarea întrebare: " + userMessage);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "AI-ul nu a returnat niciun text.";

        } catch (Exception e) {
            System.err.println("❌ Eroare Gemini API: " + e.getMessage());
            return "Îmi pare rău, momentan nu pot contacta creierul digital. Te rog încearcă mai târziu.";
        }
    }
}
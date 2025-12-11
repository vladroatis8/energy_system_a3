package ro.utcluj.ds.websocket_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    private static final String API_KEY = "AIzaSyCFnBe3skiGU28HokoGyX9qgbAFrYybClw";
    
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + API_KEY;

    private final RestTemplate restTemplate;

    public LLMService() {
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public String generateResponse(String userMessage) {
        try {
            Map<String, String> part = new HashMap<>();
            part.put("text", "Ești un asistent util pentru o companie de energie. Răspunde scurt și concis în limba română la această cerere: " + userMessage);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

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
            return "AI-ul nu a răspuns.";

        } catch (HttpClientErrorException e) {
            // Dacă nici 2.0 nu merge, vedem exact ce modele ai disponibile
            if (e.getStatusCode().value() == 404) {
                System.out.println("⚠️ Modelul 2.0 nu a fost găsit. Listăm modelele disponibile...");
                listAvailableModels(); 
                return "Eroare: Model indisponibil. Vezi consola pentru lista de modele.";
            }
            return "Eroare AI: " + e.getStatusText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Eroare internă.";
        }
    }

    private void listAvailableModels() {
        try {
            String listUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY;
            ResponseEntity<String> response = restTemplate.getForEntity(listUrl, String.class);
            System.out.println("📋 MODELE DISPONIBILE (Copiază un 'name' de aici în API_URL):");
            System.out.println(response.getBody());
        } catch (Exception ex) {
            System.err.println("Nu pot lista modelele: " + ex.getMessage());
        }
    }
}
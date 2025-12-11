package ro.utcluj.ds.websocket_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ro.utcluj.ds.websocket_service.dto.ChatMessage;
import ro.utcluj.ds.websocket_service.service.LLMService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    @Autowired
    private LLMService llmService;

    // Memorie temporară: Ținem minte dacă un user este în modul AI sau nu
    // Key = User/Sender, Value = true (AI Mode) / false (Rule Mode)
    private final Map<String, Boolean> aiModeActive = new ConcurrentHashMap<>();

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        String sender = message.getSender();
        String content = message.getContent();
        
        // Verificăm și procesăm mesajul
        String responseContent = processRequest(sender, content);
        
        return new ChatMessage("System Chatbot", responseContent);
    }

    private String processRequest(String sender, String content) {
        if (content == null) return "Te rog scrie ceva.";
        String msg = content.trim().toLowerCase();

        // 1. COMANDĂ DE ACTIVARE AI
        if (msg.equals("ai") || msg.equals("chatgpt") || msg.equals("gemini")) {
            aiModeActive.put(sender, true);
            return "✅ Modul AI Activat! Acum vorbești cu asistentul inteligent Gemini. Scrie 'stop' pentru a reveni la meniu.";
        }

        // 2. COMANDĂ DE DEZACTIVARE AI
        if (msg.equals("stop") || msg.equals("exit") || msg.equals("rules")) {
            aiModeActive.put(sender, false);
            return "🛑 Modul AI Dezactivat. Ai revenit la asistentul standard.";
        }

        // 3. VERIFICĂM ÎN CE MOD ESTE UTILIZATORUL
        boolean isAiMode = aiModeActive.getOrDefault(sender, false);

        if (isAiMode) {
            // --- MODUL AI ---
            // Orice scrie utilizatorul este trimis la LLM, fără să verificăm reguli
            System.out.println("🤖 AI Mode request de la " + sender + ": " + content);
            return llmService.generateResponse(content);
        } else {
            // --- MODUL REGULI (RULE BASED) ---
            return processRules(msg);
        }
    }

    private String processRules(String msg) {
        // Aici sunt cele 10 reguli OBLIGATORII pentru nota 5
        // Acum sunt "safe" pentru că nu se activează când userul vrea AI.

        if (msg.contains("salut") || msg.contains("buna")) {
            return "Salut! Scrie 'ajutor' pentru comenzi sau 'ai' pentru inteligența artificială.";
        }

        if (msg.contains("device") || msg.contains("dispozitiv")) {
            return "Gestionează dispozitivele în pagina 'Devices'.";
        }

        if (msg.contains("consum")) {
            return "Vezi consumul detaliat în pagina 'Charts'.";
        }

        if (msg.contains("pret") || msg.contains("cost")) {
            return "Costul energiei depinde de contractul tău.";
        }

        if (msg.contains("admin") || msg.contains("suport")) {
            return "Contact: admin@energy.com";
        }

        if (msg.contains("factura")) {
            return "Factura se emite la final de lună.";
        }

        if (msg.contains("ore") || msg.contains("timp")) {
            return "Datele se actualizează orar.";
        }

        if (msg.contains("cont") || msg.contains("parola")) {
            return "Nu da parola nimănui!";
        }

        if (msg.contains("multumesc")) {
            return "Cu plăcere!";
        }
        
        if (msg.contains("ajutor")) {
             return "Comenzi: 1.consum, 2.device, 4.factura, 5.ore, 6.cont, 7.admin, 8.pret, 9.parola, 10.multumesc SAU scrie 'ai' pentru a vorbi cu asistentul AI.";
        }

        // Fallback pentru modul standard
        return "Comandă necunoscută. Scrie 'ajutor' pentru reguli sau 'ai' pentru asistentul inteligent.";
    }
}
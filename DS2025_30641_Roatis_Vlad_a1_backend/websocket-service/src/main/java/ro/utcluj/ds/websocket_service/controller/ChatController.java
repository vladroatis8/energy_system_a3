package ro.utcluj.ds.websocket_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ro.utcluj.ds.websocket_service.dto.ChatMessage;
import ro.utcluj.ds.websocket_service.service.LLMService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, Boolean> aiModeActive = new ConcurrentHashMap<>();

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        String sender = message.getSender();
        String content = message.getContent();

        System.out.println("📩 Mesaj: " + sender + " -> " + content);

        // 1. Daca userul cere ADMIN
        if (content.trim().equalsIgnoreCase("admin")) {
            aiModeActive.put(sender, false); // Oprim AI-ul
            
            // Trimitem alerta DOAR pe canalul privat al adminilor
            ChatMessage alert = new ChatMessage(sender, "⚠️ Userul " + sender + " cere ajutor!");
            messagingTemplate.convertAndSend("/topic/admin", alert);
            
            // NU mai trimitem nimic înapoi pe /topic/messages
            // Frontend-ul va afișa singur "Am anunțat adminul".
            return;
        }

        // 2. Modul AI
        if (aiModeActive.getOrDefault(sender, false) || content.equalsIgnoreCase("ai")) {
            if (content.equalsIgnoreCase("ai")) aiModeActive.put(sender, true);
            else if (content.equalsIgnoreCase("stop")) aiModeActive.put(sender, false);
            
            // Procesam cu AI
            String aiResponse = llmService.generateResponse(content);
            // AI-ul raspunde Public (simplificare) sau Private (daca ai implementa SendToUser)
            // Aici trimitem pe public, dar frontend-ul va filtra.
            messagingTemplate.convertAndSend("/topic/messages", new ChatMessage("System Chatbot", aiResponse));
            return;
        }

        // 3. Mesaj Chat Normal (Client -> Admin)
        // Clientul scrie un mesaj, el trebuie să ajungă la Admin
        // Il trimitem pe topicul de admin
        ChatMessage msgForAdmin = new ChatMessage(sender, content);
        messagingTemplate.convertAndSend("/topic/admin", msgForAdmin);
    }

    // Adminul răspunde
   @MessageMapping("/admin/reply")
    public void adminReply(@Payload ChatMessage message) {
        // În loc să hardcodăm "ADMIN", luăm numele adminului (ex: "vldadmin") 
        // și îi punem prefixul "ADMIN " ca să știe clientul cu cine vorbește.
        String senderWithTag = "ADMIN " + message.getSender();
        
        ChatMessage response = new ChatMessage(senderWithTag, message.getContent());
        messagingTemplate.convertAndSend("/topic/messages", response);
    }
}
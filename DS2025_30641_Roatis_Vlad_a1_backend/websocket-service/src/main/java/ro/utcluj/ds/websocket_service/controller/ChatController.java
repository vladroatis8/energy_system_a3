package ro.utcluj.ds.websocket_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    // Ținem minte dacă un user vrea să vorbească cu AI-ul
    private final Map<String, Boolean> aiModeActive = new ConcurrentHashMap<>();
    
    // Ținem minte dacă un user este într-o sesiune cu un Admin
    private final Map<String, Boolean> adminSessionActive = new ConcurrentHashMap<>();

    // 1. Endpoint-ul principal unde trimit toți userii mesaje
    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        String sender = message.getSender();
        String content = message.getContent();

        System.out.println("📩 Mesaj primit de la " + sender + ": " + content);

        // --- SCENARIUL 1: MESAJ DE LA ADMIN ---
        // Dacă sender-ul este 'admin', trimitem mesajul specific către userul țintă (dacă am avea ID)
        // Pentru simplitate (broadcast), adminul răspunde pe canalul public, dar prefixat.
        if ("admin".equalsIgnoreCase(sender)) {
            // Adminul trimite un mesaj global sau către un user specific. 
            // Aici simplificăm: Adminul vorbește pe topicul public, toți îl văd.
            ChatMessage response = new ChatMessage("ADMIN", content);
            messagingTemplate.convertAndSend("/topic/messages", response);
            return;
        }

        // --- SCENARIUL 2: USER VREA ADMIN ---
        if (content.trim().equalsIgnoreCase("admin") || content.trim().equalsIgnoreCase("help")) {
            adminSessionActive.put(sender, true);
            aiModeActive.put(sender, false); // Oprim AI-ul
            
            // Trimitem notificare către Admini
            ChatMessage alert = new ChatMessage("SYSTEM", "Userul " + sender + " cere ajutor!");
            messagingTemplate.convertAndSend("/topic/admin", alert);
            
            // Răspuns către User
            ChatMessage reply = new ChatMessage("System", "Un administrator a fost notificat. Te rog așteaptă.");
            messagingTemplate.convertAndSend("/topic/messages", reply); // Simplificare: trimitem pe topicul comun
            return;
        }

        // --- SCENARIUL 3: SESIUNE ACTIVĂ CU ADMIN ---
        if (adminSessionActive.getOrDefault(sender, false)) {
            // Mesajul userului se duce direct la Admini
            ChatMessage msgForAdmin = new ChatMessage(sender, content);
            messagingTemplate.convertAndSend("/topic/admin", msgForAdmin);
            return; 
        }

        // --- SCENARIUL 4: AI MODE SAU REGULI ---
        String responseContent = processRequest(sender, content);
        ChatMessage response = new ChatMessage("System Chatbot", responseContent);
        
        // Trimitem răspunsul (în mod real ar trebui trimis doar Userului, dar aici e demo public)
        messagingTemplate.convertAndSend("/topic/messages", response);
    }

    // --- Endpoint special pentru Admini să trimită mesaje ---
    @MessageMapping("/admin/reply")
    public void adminReply(@Payload ChatMessage message) {
        // Adminul trimite un mesaj care trebuie să ajungă la Useri (pe topicul public /topic/messages)
        ChatMessage response = new ChatMessage("ADMIN", message.getContent());
        messagingTemplate.convertAndSend("/topic/messages", response);
    }

    private String processRequest(String sender, String content) {
        if (content == null) return "...";
        String msg = content.trim().toLowerCase();

        // Comenzi switch
        if (msg.equals("ai")) {
            aiModeActive.put(sender, true);
            return "✅ Modul AI Activat!";
        }
        if (msg.equals("stop")) {
            aiModeActive.put(sender, false);
            adminSessionActive.put(sender, false); // Iese și din modul Admin
            return "🛑 Moduri speciale dezactivate.";
        }

        if (aiModeActive.getOrDefault(sender, false)) {
            return llmService.generateResponse(content);
        }

        // Reguli hardcodate (cele vechi)
        if (msg.contains("salut")) return "Salut! Scrie 'admin' pentru suport uman sau 'ai' pentru bot.";
        if (msg.contains("device")) return "Gestionează device-urile în tab-ul dedicat.";
        // ... (restul regulilor tale) ...
        
        return "Comandă necunoscută. Scrie 'ai' sau 'admin'.";
    }
}
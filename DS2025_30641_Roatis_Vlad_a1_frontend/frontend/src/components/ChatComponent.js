import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const ChatComponent = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [connected, setConnected] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    
    // Stare internă pentru User și dacă e Admin
    const [currentUser, setCurrentUser] = useState("Guest");
    const [isAdmin, setIsAdmin] = useState(false);

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    // --- 1. MONITORIZARE LOGIN (Rulează la fiecare secundă) ---
    useEffect(() => {
        const checkIdentity = () => {
            // Citim direct din browser ce a salvat pagina de Login
            const storedName = localStorage.getItem("username") || "Guest";
            const storedRole = localStorage.getItem("role"); // Ex: "ADMINISTRATOR"

            // AICI ERA PROBLEMA: Verificăm dacă rolul este ADMINISTRATOR
            const isUserAdmin = storedRole === "ADMIN" || storedRole === "ADMINISTRATOR";

            // Dacă s-a schimbat ceva față de ce știe chat-ul acum, actualizăm
            if (storedName !== currentUser || isUserAdmin !== isAdmin) {
                console.log(`🔄 Schimbare utilizator detectată: ${storedName} (Admin: ${isUserAdmin})`);
                setCurrentUser(storedName);
                setIsAdmin(isUserAdmin);
                setMessages([]); // Golim chatul vechi
            }
        };

        const interval = setInterval(checkIdentity, 1000); // Verifică la fiecare 1s
        checkIdentity(); // Verifică și imediat la încărcare

        return () => clearInterval(interval);
    }, [currentUser, isAdmin]);


    // --- 2. CONEXIUNEA WEBSOCKET (Se reface automat când se schimbă userul) ---
    useEffect(() => {
        // Opțional: Nu ne conectăm dacă e Guest (sau lăsăm doar pentru AI)
        // if (currentUser === "Guest") return;

        const client = new Client({
            // URL-ul prin Traefik
            webSocketFactory: () => new SockJS('http://localhost/ws'),
            
            onConnect: () => {
                console.log(`✅ Chat conectat ca: ${currentUser}`);
                setConnected(true);

                // 1. Canal Public (Răspunsuri AI, Răspunsuri de la Admin către mine)
                client.subscribe('/topic/messages', (message) => {
                    const body = JSON.parse(message.body);
                    addMessage(body.sender, body.content);
                });

                // 2. Canal Privat Admin (Doar dacă ești Admin)
                // Aici vin mesajele de la userii care scriu "admin"
                if (isAdmin) {
                    console.log("🛡️ Mod Admin: Abonare la canalul de suport.");
                    client.subscribe('/topic/admin', (message) => {
                        const body = JSON.parse(message.body);
                        addMessage(`USER-SUPPORT (${body.sender})`, body.content);
                    });
                }
            },
            onStompError: (frame) => console.error("Eroare WebSocket:", frame),
            reconnectDelay: 5000, 
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (client.active) client.deactivate();
        };
    }, [currentUser, isAdmin]); // <--- Se re-execută când devii Admin

    // Auto-scroll
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const addMessage = (sender, content) => {
        setMessages((prev) => [...prev, { sender, content }]);
    };

    const sendMessage = () => {
        if (input.trim() && stompClientRef.current && connected) {
            const chatMessage = {
                // Dacă sunt admin, trimit cu numele "admin", altfel cu username-ul meu
                sender: isAdmin ? "admin" : currentUser, 
                content: input
            };

            // LOGICA DE RUTARE:
            // Admin -> trimite Reply (ajunge la toată lumea/user)
            // User -> trimite Chat (backend-ul decide dacă e pt AI sau pt Admin)
            const destination = isAdmin ? "/app/admin/reply" : "/app/chat";

            stompClientRef.current.publish({
                destination: destination,
                body: JSON.stringify(chatMessage)
            });
            setInput('');
        }
    };

    // --- STILURI VIZUALE ---
    const headerColor = isAdmin ? '#dc3545' : '#007bff'; // Roșu (Admin) vs Albastru (Client)

    const styles = {
        container: { position: 'fixed', bottom: '20px', right: '20px', zIndex: 1000, display: 'flex', flexDirection: 'column', alignItems: 'flex-end' },
        button: { backgroundColor: headerColor, color: 'white', border: 'none', borderRadius: '50%', width: '60px', height: '60px', cursor: 'pointer', boxShadow: '0 4px 8px rgba(0,0,0,0.2)', fontSize: '24px', display: 'flex', justifyContent: 'center', alignItems: 'center' },
        chatWindow: { width: '350px', height: '500px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 5px 15px rgba(0,0,0,0.3)', display: 'flex', flexDirection: 'column', overflow: 'hidden', marginBottom: '15px', border: '1px solid #ddd' },
        header: { backgroundColor: headerColor, color: 'white', padding: '15px', fontWeight: 'bold', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
        messagesArea: { flex: 1, padding: '15px', overflowY: 'auto', backgroundColor: '#f9f9f9', display: 'flex', flexDirection: 'column', gap: '10px' },
        messageBubble: (sender) => {
            // Mesajul e "al meu" dacă numele coincide sau dacă sunt admin și expeditorul e "admin"
            const isMe = sender === currentUser || (isAdmin && sender === 'admin') || sender === 'Me';
            const isSupport = sender.startsWith("USER-SUPPORT"); // Mesaje de la useri către admin
            
            let bg = isMe ? headerColor : (isSupport ? '#ffc107' : '#e9ecef');
            let txt = isMe ? 'white' : 'black';
            return { maxWidth: '80%', padding: '10px', borderRadius: '10px', alignSelf: isMe ? 'flex-end' : 'flex-start', backgroundColor: bg, color: txt, fontSize: '14px', boxShadow: '0 1px 2px rgba(0,0,0,0.1)' };
        },
        inputArea: { padding: '10px', borderTop: '1px solid #ddd', display: 'flex', gap: '10px', backgroundColor: 'white' },
        input: { flex: 1, padding: '8px', borderRadius: '5px', border: '1px solid #ccc', outline: 'none' },
        sendBtn: { padding: '8px 15px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }
    };

    return (
        <div style={styles.container}>
            {isOpen && (
                <div style={styles.chatWindow}>
                    <div style={styles.header}>
                        <span>{isAdmin ? "🛡️ ADMIN PANEL" : "💬 Asistent Energie"}</span>
                        <button onClick={() => setIsOpen(false)} style={{background: 'transparent', border: 'none', color: 'white', cursor: 'pointer'}}>✖</button>
                    </div>
                    <div style={styles.messagesArea}>
                        {messages.length === 0 && (
                            <div style={{textAlign: 'center', color: '#888', marginTop: '50%', padding: '0 20px'}}>
                                {isAdmin 
                                    ? "Aștept solicitări de la clienți..." 
                                    : <span>Salut <b>{currentUser}</b>! Scrie <b>"admin"</b> pentru suport uman sau <b>"ai"</b> pentru bot.</span>
                                }
                            </div>
                        )}
                        {messages.map((msg, index) => (
                            <div key={index} style={styles.messageBubble(msg.sender)}>
                                <div style={{fontSize: '10px', fontWeight: 'bold', marginBottom: '2px', opacity: 0.8}}>
                                    {msg.sender === currentUser || (isAdmin && msg.sender === 'admin') ? "Eu" : msg.sender}
                                </div>
                                {msg.content}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                    <div style={styles.inputArea}>
                        <input style={styles.input} value={input} onChange={(e) => setInput(e.target.value)} onKeyPress={(e) => e.key === 'Enter' && sendMessage()} placeholder="Scrie mesaj..." />
                        <button style={styles.sendBtn} onClick={sendMessage}>➤</button>
                    </div>
                </div>
            )}
            <button style={styles.button} onClick={() => setIsOpen(!isOpen)}>
                {isOpen ? '⬇' : (isAdmin ? '🛡️' : '💬')}
            </button>
        </div>
    );
};

export default ChatComponent;
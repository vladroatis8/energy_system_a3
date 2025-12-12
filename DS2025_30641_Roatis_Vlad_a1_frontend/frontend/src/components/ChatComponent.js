import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const ChatComponent = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [connected, setConnected] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    
    // Stare pentru User și Rol
    const [currentUser, setCurrentUser] = useState("Guest");
    const [isAdmin, setIsAdmin] = useState(false);

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    // --- 1. MONITORIZARE LOGIN ---
    useEffect(() => {
        const checkIdentity = () => {
            // Citim username-ul salvat la login. Dacă nu există, e Guest.
            const storedName = localStorage.getItem("username") || "Guest";
            const storedRole = localStorage.getItem("role");

            const isUserAdmin = storedRole === "ADMIN" || storedRole === "ADMINISTRATOR";

            if (storedName !== currentUser || isUserAdmin !== isAdmin) {
                console.log(`🔄 User: ${storedName}, Admin: ${isUserAdmin}`);
                setCurrentUser(storedName);
                setIsAdmin(isUserAdmin);
                setMessages([]); 
            }
        };

        const interval = setInterval(checkIdentity, 1000);
        checkIdentity(); 

        return () => clearInterval(interval);
    }, [currentUser, isAdmin]);

    // --- 2. CONEXIUNE WEBSOCKET ---
    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost/ws'),
            onConnect: () => {
                setConnected(true);

                // A. CANAL PUBLIC (Aici vin mesajele vizibile tuturor)
                client.subscribe('/topic/messages', (message) => {
                    const body = JSON.parse(message.body);
                    
                    // --- FILTRARE PENTRU A EVITA DUBLURILE ---
                    
                    // 1. Dacă sunt Admin și mesajul vine de la "ADMIN" (adică de la mine via server), îl ignor
                    //    pentru că l-am afișat deja local când am dat Send.
                    if (isAdmin && (body.sender === "ADMIN" || body.sender === "admin")) {
                        return; 
                    }

                    // 2. Dacă sunt Client și mesajul vine de la mine (numele meu), îl ignor
                    //    (tot pentru că l-am afișat local).
                    if (!isAdmin && body.sender === currentUser) {
                        return;
                    }

                    // Dacă trece de filtre, îl afișez
                    addMessage(body.sender, body.content);
                });

                // B. CANAL PRIVAT ADMIN (Doar Adminii văd cererile de suport)
                if (isAdmin) {
                    client.subscribe('/topic/admin', (message) => {
                        const body = JSON.parse(message.body);
                        // Aici afișăm mesajele care vin de la useri pe canalul de suport
                        addMessage(`USER-SUPPORT (${body.sender})`, body.content);
                    });
                }
            },
            onStompError: (frame) => console.error("Eroare WS:", frame),
            reconnectDelay: 5000, 
        });

        client.activate();
        stompClientRef.current = client;

        return () => { if (client.active) client.deactivate(); };
    }, [currentUser, isAdmin]);

    // Auto-scroll
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const addMessage = (sender, content) => {
        setMessages((prev) => [...prev, { sender, content }]);
    };

    const sendMessage = () => {
        if (input.trim() && stompClientRef.current && connected) {
            
            // Numele cu care trimit la server
            const senderName = isAdmin ? "ADMIN" : currentUser;
            
            const chatMessage = {
                sender: senderName,
                content: input
            };

            const destination = isAdmin ? "/app/admin/reply" : "/app/chat";

            // 1. Trimit la server
            stompClientRef.current.publish({
                destination: destination,
                body: JSON.stringify(chatMessage)
            });

            // 2. Afișez LOCAL (ca să văd ce am scris imediat)
            // Aici e cheia: pentru că îl adaug aici, trebuie să îl ignor când vine înapoi de la server (vezi sus)
            addMessage("Eu", input);
            
            setInput('');
        }
    };

    // --- STILURI ---
    const headerColor = isAdmin ? '#dc3545' : '#007bff'; 

    const styles = {
        container: { 
            position: 'fixed', bottom: '20px', right: '20px', zIndex: 9999,
            display: 'flex', flexDirection: 'column', alignItems: 'flex-end' 
        },
        button: { 
            backgroundColor: headerColor, color: 'white', border: 'none', 
            borderRadius: '50%', width: '60px', height: '60px', 
            cursor: 'pointer', boxShadow: '0 4px 10px rgba(0,0,0,0.3)', 
            fontSize: '28px', display: 'flex', justifyContent: 'center', alignItems: 'center' 
        },
        chatWindow: { 
            width: '350px', height: '500px',
            backgroundColor: 'white', borderRadius: '12px', 
            boxShadow: '0 8px 24px rgba(0,0,0,0.2)', 
            display: 'flex', flexDirection: 'column', overflow: 'hidden', 
            marginBottom: '16px', border: '1px solid #ddd' 
        },
        header: { 
            backgroundColor: headerColor, color: 'white', padding: '16px', 
            fontWeight: 'bold', fontSize: '16px',
            display: 'flex', justifyContent: 'space-between', alignItems: 'center' 
        },
        messagesArea: { 
            flex: 1, padding: '16px', overflowY: 'auto', backgroundColor: '#f5f7fb', 
            display: 'flex', flexDirection: 'column', gap: '12px' 
        },
        inputArea: { 
            padding: '12px', borderTop: '1px solid #eee', display: 'flex', gap: '8px', backgroundColor: 'white' 
        },
        input: { 
            flex: 1, padding: '10px', borderRadius: '20px', border: '1px solid #ccc', outline: 'none' 
        },
        sendBtn: { 
            padding: '10px 16px', backgroundColor: '#28a745', color: 'white', 
            border: 'none', borderRadius: '20px', cursor: 'pointer', fontWeight: 'bold'
        },
        bubble: (sender) => {
            const isMe = sender === "Eu";
            const isSupport = sender.startsWith("USER-SUPPORT");
            const isSystem = sender === "System Chatbot";

            let align = isMe ? 'flex-end' : 'flex-start';
            let bg = isMe ? headerColor : 'white';
            let txt = isMe ? 'white' : '#333';
            
            if (isSupport) { bg = '#ffeeba'; txt = '#856404'; } 
            if (isSystem) { bg = '#e2e3e5'; txt = '#383d41'; } 

            return {
                maxWidth: '75%', padding: '10px 14px', borderRadius: '18px',
                alignSelf: align, backgroundColor: bg, color: txt,
                fontSize: '14px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
                borderBottomRightRadius: isMe ? '4px' : '18px',
                borderBottomLeftRadius: isMe ? '18px' : '4px',
                wordWrap: 'break-word'
            };
        }
    };

    return (
        <div style={styles.container}>
            {isOpen && (
                <div style={styles.chatWindow}>
                    <div style={styles.header}>
                        <span>{isAdmin ? "🛡️ ADMIN PANEL" : "💬 Asistent Energie"}</span>
                        <button onClick={() => setIsOpen(false)} style={{background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', fontSize: '18px'}}>✖</button>
                    </div>
                    
                    <div style={styles.messagesArea}>
                        {/* MESAJUL DE INTAMPINARE CLAR */}
                        {messages.length === 0 && (
                            <div style={{textAlign: 'center', color: '#666', marginTop: '40%', padding: '0 20px', fontSize: '14px', lineHeight: '1.6'}}>
                                {isAdmin ? (
                                    <div>Aștept tichete de la clienți...</div>
                                ) : (
                                    <div>
                                        <p>Salut <b>{currentUser}</b>!</p>
                                        <p>Scrie una dintre opțiuni:</p>
                                        <ul style={{listStyle: 'none', padding: 0, fontWeight: 'bold', color: '#007bff'}}>
                                            <li>"ajutor" - Comenzi simple</li>
                                            <li>"ai" - Asistent Inteligent</li>
                                            <li>"admin" - Suport Uman</li>
                                        </ul>
                                    </div>
                                )}
                            </div>
                        )}
                        {messages.map((msg, index) => (
                            <div key={index} style={styles.bubble(msg.sender)}>
                                {msg.sender !== "Eu" && 
                                    <div style={{fontSize: '10px', fontWeight: 'bold', marginBottom: '4px', opacity: 0.7}}>
                                        {msg.sender}
                                    </div>
                                }
                                {msg.content}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>

                    <div style={styles.inputArea}>
                        <input 
                            style={styles.input} 
                            value={input} 
                            onChange={(e) => setInput(e.target.value)} 
                            onKeyPress={(e) => e.key === 'Enter' && sendMessage()} 
                            placeholder="Scrie un mesaj..." 
                        />
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
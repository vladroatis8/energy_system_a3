import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const ChatComponent = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [connected, setConnected] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
     //initializare stare utilizator
    const [currentUser, setCurrentUser] = useState(() => {
        return localStorage.getItem("username") || "Guest";
    });
    
    const [isAdmin, setIsAdmin] = useState(() => {
        const role = localStorage.getItem("role");
        return role === "ADMIN" || role === "ADMINISTRATOR";
    });

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);
    // monitorizare login
    useEffect(() => {
        const interval = setInterval(() => {
            const storedName = localStorage.getItem("username") || "Guest";
            const storedRole = localStorage.getItem("role");
            const isUserAdmin = storedRole === "ADMIN" || storedRole === "ADMINISTRATOR";

            if (storedName !== currentUser || isUserAdmin !== isAdmin) {
                console.log(`🔄 Schimbare cont: ${storedName} (Admin: ${isUserAdmin})`);
                setCurrentUser(storedName);
                setIsAdmin(isUserAdmin);
                setMessages([]); 
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [currentUser, isAdmin]);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost/ws'),
            onConnect: () => {
                console.log("✅ WebSocket Conectat!");
                setConnected(true);

                // Canal Public
                client.subscribe('/topic/messages', (message) => {
                    const body = JSON.parse(message.body);
                    
                    
                    
                    if (isAdmin && body.sender === `ADMIN ${currentUser}`) {
                        return; 
                    }

                    if (!isAdmin && body.sender === currentUser) {
                        return;
                    }

                    addMessage(body.sender, body.content);
                });

                // Canal Admin
                if (isAdmin) {
                    client.subscribe('/topic/admin', (message) => {
                        const body = JSON.parse(message.body);
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

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const addMessage = (sender, content) => {
        setMessages((prev) => [...prev, { sender, content }]);
    };

    const sendMessage = () => {
        if (input.trim() && stompClientRef.current && connected) {
            
            const senderName = currentUser;
            
            const chatMessage = {
                sender: senderName,
                content: input
            };

            const destination = isAdmin ? "/app/admin/reply" : "/app/chat";

            stompClientRef.current.publish({
                destination: destination,
                body: JSON.stringify(chatMessage)
            });

            addMessage(senderName, input);
            
            setInput('');
        }
    };

    // STILURI
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
            // daca mesajul e al meu
            const isMe = sender === currentUser;
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
                        {messages.length === 0 && (
                            <div style={{textAlign: 'center', color: '#666', marginTop: '40%', padding: '0 20px', fontSize: '14px', lineHeight: '1.6'}}>
                                {isAdmin ? (
                                    <div>Aștept mesaje de la clienți...</div>
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
                                {/*afisam numele */}
                                {msg.sender !== currentUser && 
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
import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs'; // Importăm librăria nouă

const ChatComponent = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [connected, setConnected] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    
    // Folosim useRef pentru a păstra instanța clientului între render-uri
    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        // Configurăm clientul STOMP modern
        const client = new Client({
            // Folosim SockJS factory pentru a ne conecta la URL-ul Traefik
            webSocketFactory: () => new SockJS('http://localhost/ws'),
            
            // Funcția apelată la conectarea cu succes
            onConnect: (frame) => {
                console.log("✅ WebSocket Connected");
                setConnected(true);

                // Ne abonăm la topic
                client.subscribe('/topic/messages', (message) => {
                    const body = JSON.parse(message.body);
                    addMessage(body.sender, body.content);
                });
            },

            // Funcția apelată în caz de eroare
            onStompError: (frame) => {
                console.error('❌ Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
            
            // Opțional: încearcă să se reconecteze automat dacă cade serverul
            reconnectDelay: 5000,
        });

        // Activăm conexiunea
        client.activate();
        stompClientRef.current = client;

        // Cleanup: Deconectăm la închiderea componentei
        return () => {
            client.deactivate();
        };
    }, []);

    // Scroll automat la ultimul mesaj
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const addMessage = (sender, content) => {
        setMessages((prev) => [...prev, { sender, content }]);
    };

    const sendMessage = () => {
        if (input.trim() && stompClientRef.current && connected) {
            const chatMessage = {
                sender: "Eu", 
                content: input
            };

            // Sintaxa nouă pentru trimitere (publish)
            stompClientRef.current.publish({
                destination: "/app/chat",
                body: JSON.stringify(chatMessage)
            });

            setInput('');
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') sendMessage();
    };

    // --- STILURILE RĂMÂN NESCHIMBATE ---
    const styles = {
        container: { position: 'fixed', bottom: '20px', right: '20px', zIndex: 1000, display: 'flex', flexDirection: 'column', alignItems: 'flex-end' },
        button: { backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '50%', width: '60px', height: '60px', cursor: 'pointer', boxShadow: '0 4px 8px rgba(0,0,0,0.2)', fontSize: '24px', display: 'flex', justifyContent: 'center', alignItems: 'center' },
        chatWindow: { width: '350px', height: '500px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 5px 15px rgba(0,0,0,0.3)', display: 'flex', flexDirection: 'column', overflow: 'hidden', marginBottom: '15px', border: '1px solid #ddd' },
        header: { backgroundColor: '#007bff', color: 'white', padding: '15px', fontWeight: 'bold', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
        messagesArea: { flex: 1, padding: '15px', overflowY: 'auto', backgroundColor: '#f9f9f9', display: 'flex', flexDirection: 'column', gap: '10px' },
        messageBubble: (sender) => ({ maxWidth: '80%', padding: '10px', borderRadius: '10px', alignSelf: sender === 'Eu' ? 'flex-end' : 'flex-start', backgroundColor: sender === 'Eu' ? '#007bff' : '#e9ecef', color: sender === 'Eu' ? 'white' : 'black', fontSize: '14px', boxShadow: '0 1px 2px rgba(0,0,0,0.1)' }),
        inputArea: { padding: '10px', borderTop: '1px solid #ddd', display: 'flex', gap: '10px', backgroundColor: 'white' },
        input: { flex: 1, padding: '8px', borderRadius: '5px', border: '1px solid #ccc', outline: 'none' },
        sendBtn: { padding: '8px 15px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' },
        senderName: { fontSize: '10px', marginBottom: '2px', opacity: 0.7, textAlign: 'right' }
    };

    return (
        <div style={styles.container}>
            {isOpen && (
                <div style={styles.chatWindow}>
                    <div style={styles.header}>
                        <span>💬 Asistent Energie</span>
                        <button onClick={() => setIsOpen(false)} style={{background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', fontSize: '16px'}}>✖</button>
                    </div>
                    
                    <div style={styles.messagesArea}>
                        {messages.length === 0 && (
                    <div style={{textAlign: 'center', color: '#888', marginTop: '50%', padding: '0 20px'}}>
                        Salut! Scrie <b>"ajutor"</b> pentru comenzi standard sau <b>"ai"</b> pentru a vorbi cu Asistentul Inteligent.
                    </div>
                        )}
                        {messages.map((msg, index) => (
                            <div key={index} style={styles.messageBubble(msg.sender)}>
                                <div style={{fontWeight: 'bold', fontSize: '11px', marginBottom: '3px'}}>
                                    {msg.sender}
                                </div>
                                {msg.content}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>

                    <div style={styles.inputArea}>
                        <input
                            style={styles.input}
                            placeholder={connected ? "Scrie un mesaj..." : "Se conectează..."}
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyPress={handleKeyPress}
                            disabled={!connected}
                        />
                        <button style={styles.sendBtn} onClick={sendMessage} disabled={!connected}>➤</button>
                    </div>
                </div>
            )}

            <button style={styles.button} onClick={() => setIsOpen(!isOpen)}>
                {isOpen ? '⬇' : '💬'}
            </button>
        </div>
    );
};

export default ChatComponent;
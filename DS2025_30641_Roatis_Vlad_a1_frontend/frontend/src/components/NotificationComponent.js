import React, { useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs'; 
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const NotificationComponent = () => {
    const ENDPOINT = "http://localhost:8085/ws";

    useEffect(() => {
        
        const client = new Client({
            webSocketFactory: () => new SockJS(ENDPOINT),
            
            onConnect: (frame) => {
                console.log("WebSocket Connected!");
                
                client.subscribe('/topic/socket/overconsumption', (message) => {
                    if (message.body) {
                        const notification = JSON.parse(message.body);
                        
                        toast.error(`⚠️ ALERTA: ${notification.message} (Device: ${notification.deviceId})`, {
                            position: "top-right",
                            autoClose: 5000,
                            hideProgressBar: false,
                            closeOnClick: true,
                            pauseOnHover: true,
                            draggable: true,
                        });
                    }
                });
            },
            
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
            
            reconnectDelay: 5000, 
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    return (
        <ToastContainer />
    );
};

export default NotificationComponent;
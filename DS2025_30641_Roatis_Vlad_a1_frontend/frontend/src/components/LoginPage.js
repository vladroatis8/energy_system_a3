import React, { useState } from 'react';
import axios from 'axios';

// 1. Primim "onLoginSuccess" ca "prop" (proprietate) din App.js
function LoginPage({ onLoginSuccess }) { 
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            // 2. Apelăm backend-ul (asigură-te că Docker rulează)
            const response = await axios.post('http://localhost/auth/login', {
                username: username,
                password: password
            });

            // 3. Salvăm token-ul și rolul în memoria browser-ului
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('role', response.data.role);

            // 4. Anunțăm părintele (App.js) că am terminat!
            // Acesta va schimba starea 'isLoggedIn' în 'true'
            onLoginSuccess(); 

        } catch (err) {
            console.error('Eroare la login:', err);
            setError('Nume de utilizator sau parolă incorectă.');
        }
    };

    // Partea de HTML (JSX)
    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Username: </label>
                    <input 
                        type="text" 
                        value={username} 
                        onChange={(e) => setUsername(e.target.value)}
                        required 
                    />
                </div>
                <div>
                    <label>Password: </label>
                    <input 
                        type="password" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required 
                    />
                </div>
                <button type="submit">Login</button>
            </form>
            {error && <p style={{color: 'red'}}>{error}</p>}
        </div>
    );
}

export default LoginPage;
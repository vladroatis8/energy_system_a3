import React, { useState } from 'react';
import axios from 'axios';

function LoginPage({ onLoginSuccess }) { 
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axios.post('http://localhost/auth/login', {
                username: username,
                password: password
            });

            const { token, role, id } = response.data;

            localStorage.setItem('token', token);
            localStorage.setItem('role', role);
            localStorage.setItem('authId', id); 

            
            onLoginSuccess(); 

        } catch (err) {
            console.error('Eroare la login:', err);
            setError('Nume de utilizator sau parola incorecta.');
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h2>Login</h2>
            <form onSubmit={handleLogin} style={{ display: 'inline-block', textAlign: 'left' }}>
                <div style={{ marginBottom: '10px' }}>
                    <label>Username: </label>
                    <input 
                        type="text" 
                        value={username} 
                        onChange={(e) => setUsername(e.target.value)}
                        required 
                        style={{ marginLeft: '10px' }}
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>Password: </label>
                    <input 
                        type="password" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required 
                        style={{ marginLeft: '10px' }}
                    />
                </div>
                <div style={{ textAlign: 'center' }}>
                    <button type="submit">Login</button>
                </div>
            </form>
            {error && <p style={{color: 'red'}}>{error}</p>}
        </div>
    );
}

export default LoginPage;

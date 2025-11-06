// În App.js

import React, { useState, useEffect } from 'react';
import './App.css';
import LoginPage from './components/LoginPage';
import Dashboard from './components/Dashboard';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  // --- FUNCȚIE NOUĂ ---
  // Această funcție va fi apelată de LoginPage
  const handleLoginSuccess = () => {
    setIsLoggedIn(true); // Actualizează starea în părinte
  };

  return (
    <div className="App">
      <h1>Sistem de Management Energetic</h1>
      
      {isLoggedIn ? (
        <Dashboard /> 
      ) : (
        // --- MODIFICARE AICI ---
        // Pasează funcția ca "prop" (proprietate) către LoginPage
        <LoginPage onLoginSuccess={handleLoginSuccess} /> 
      )}
    </div>
  );
}

export default App;
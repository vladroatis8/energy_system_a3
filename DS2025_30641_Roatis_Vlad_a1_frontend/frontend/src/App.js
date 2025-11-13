
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

  
  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  return (
    <div className="App">
      <h1>Sistem de Management Energetic</h1>
      
      {isLoggedIn ? (
        <Dashboard /> 
      ) : (
        
        <LoginPage onLoginSuccess={handleLoginSuccess} /> 
      )}
    </div>
  );
}

export default App;
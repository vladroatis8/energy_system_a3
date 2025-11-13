import React from 'react';
import { getAuthData } from '../api';
import UserManagement from './UserManagement';
import DeviceManagement from './DeviceManagement';
import ClientDevices from './ClientDevices';

function Dashboard() {
  const authData = getAuthData(); 
  const authId = localStorage.getItem('authId'); 

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('authId'); 
    window.location.reload();
  };

  
  if (!authData) {
    handleLogout();
    return <p>Sesiune invalida. Se încarca...</p>;
  }

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h2>Dashboard</h2>
      <p>
        Rolul tau este: <strong>{authData.role}</strong><br />
        ID-ul tau de autentificare: <strong>{authData.userId}</strong>
      </p>
      <button
        onClick={handleLogout}
        style={{
          padding: '6px 12px',
          marginBottom: '20px',
          cursor: 'pointer',
          backgroundColor: '#f5f5f5',
          border: '1px solid #ccc',
          borderRadius: '4px',
        }}
      >
        Logout
      </button>
      <hr />

      {/* 🔹 ADMINISTRATOR: poate gestiona useri si device-uri */}
      {authData.role === 'ADMINISTRATOR' && (
        <div>
          <h3>Panou Administrator</h3>
          <UserManagement />
          <DeviceManagement />
        </div>
      )}

      {/* 🔹 CLIENT: vede doar device-urile proprii */}
      {authData.role === 'CLIENT' && (
        <div>
          <h3>Dispozitivele mele</h3>
          <ClientDevices />
        </div>
      )}
    </div>
  );
}

export default Dashboard;

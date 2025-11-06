import React from 'react';
import { getAuthData } from '../api';
import UserManagement from './UserManagement';
import DeviceManagement from './DeviceManagement'; // ✅ import corect
import ClientDevices from './ClientDevices';
function Dashboard() {
    const authData = getAuthData(); // Obținem datele din token

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.reload();
    };

    // Dacă token-ul e invalid -> delogare automată
    if (!authData) {
        handleLogout();
        return <p>Sesiune invalidă. Se încarcă...</p>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Dashboard</h2>
            <p>
                Rolul tău este: <strong>{authData.role}</strong>
            </p>
            <button onClick={handleLogout}>Logout</button>
            <hr />

            {/* 🔹 ADMINISTRATOR: poate gestiona useri și device-uri */}
            {authData.role === 'ADMINISTRATOR' && (
                <div>
                    <h3>Panou Administrator</h3>
                    <UserManagement />
                    <DeviceManagement /> {/* ✅ afișăm și device-urile */}
                </div>
            )}

            {/* 🔹 CLIENT: vede doar device-urile proprii */}
            {authData.role === 'CLIENT' && (
                <div>
                    <ClientDevices />  {/* ✅ aici se afișează tabelul */}
                </div>
            )}
        </div>
    );
}

export default Dashboard;

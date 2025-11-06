import React, { useEffect, useState } from 'react';
import api from '../api';
import { getAuthData } from '../api';

function ClientDevices() {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState('');
    const authData = getAuthData();

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                console.log("🔍 authData:", authData);
                const response = await api.get(`/devices/user/${authData.userId}`);
                setDevices(response.data);
                setError('');
            } catch (err) {
                console.error('Eroare la preluarea device-urilor:', err);
                setError('Nu s-au putut încărca dispozitivele.');
            }
        };

        if (authData?.userId) {
            fetchDevices();
        }
    }, [authData]);
    console.log("Dashboard authData:", authData);
    return (
        <div>
            <h3>Dispozitivele mele</h3>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {devices.length === 0 ? (
                <p>Nu ai dispozitive asignate.</p>
            ) : (
                <table border="1" style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nume</th>
                            <th>Descriere</th>
                            <th>Consum maxim</th>
                        </tr>
                    </thead>
                    <tbody>
                        {devices.map(device => (
                            <tr key={device.id}>
                                <td>{device.id}</td>
                                <td>{device.name}</td>
                                <td>{device.description}</td>
                                <td>{device.maxConsumption}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default ClientDevices;

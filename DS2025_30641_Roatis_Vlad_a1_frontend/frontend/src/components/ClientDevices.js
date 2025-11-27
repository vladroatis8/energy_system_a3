import React, { useEffect, useState } from 'react';
import api from '../api';
import { getAuthData } from '../api';
import ClientConsumption from './ClientConsumption';

function ClientDevices() {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState('');
    const [selectedDeviceId, setSelectedDeviceId] = useState(null);

    const authData = getAuthData();

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                const response = await api.get(`/devices/user/${authData.userId}`);
                setDevices(response.data);
                setError('');
            } catch (err) {
                setError('Nu s-au putut încărca dispozitivele.');
            }
        };

        if (authData?.userId) {
            fetchDevices();
        }
    }, [authData]);

    return (
        <div>
            <h2>Dispozitivele mele</h2>

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
                            <th>Actiuni</th>
                        </tr>
                    </thead>
                    <tbody>
                        {devices.map(device => (
                            <tr key={device.id}>
                                <td>{device.id}</td>
                                <td>{device.name}</td>
                                <td>{device.description}</td>
                                <td>{device.maxConsumption}</td>
                                <td>
                                    <button onClick={() => setSelectedDeviceId(device.id)}>
                                        Vezi consum
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {selectedDeviceId && (
                <div style={{ marginTop: '30px' }}>
                    <h3>Istoric consum pentru device #{selectedDeviceId}</h3>
                    <ClientConsumption deviceId={selectedDeviceId} />
                </div>
            )}
        </div>
    );
}

export default ClientDevices;

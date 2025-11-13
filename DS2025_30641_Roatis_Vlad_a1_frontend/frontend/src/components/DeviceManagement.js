import React, { useState, useEffect } from 'react';
import api from '../api';

function DeviceManagement() {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const [newName, setNewName] = useState('');
    const [newDescription, setNewDescription] = useState('');
    const [newMaxConsumption, setNewMaxConsumption] = useState('');

    const fetchDevices = async () => {
        try {
            const response = await api.get('/devices');
            setDevices(response.data);
            setError('');
        } catch (err) {
            console.error('Eroare fetch devices:', err);
            setError('Nu am putut încărca device-urile.');
        }
    };

    useEffect(() => {
        fetchDevices();
    }, []);

    const handleCreateDevice = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await api.post('/devices', {
                name: newName,
                description: newDescription,
                maxConsumption: parseFloat(newMaxConsumption)
            });
            setNewName('');
            setNewDescription('');
            setNewMaxConsumption('');
            await fetchDevices();
        } catch (err) {
            console.error('Eroare creare device:', err);
            setError('Eroare la crearea device-ului.');
        }
        setLoading(false);
    };

    const handleDeleteDevice = async (id) => {
        if (window.confirm('Esti sigur ca vrei sa stergi device-ul?')) {
            try {
                await api.delete(`/devices/${id}`);
                fetchDevices();
            } catch (err) {
                console.error('Eroare la stergere:', err);
                setError('Eroare la stergerea device-ului.');
            }
        }
    };

    const handleAssignDevice = async (deviceId) => {
        const userId = prompt('Introdu ID-ul utilizatorului:');
        if (!userId) return;
        try {
            await api.put(`/devices/${deviceId}/assign/${userId}`);
            fetchDevices();
        } catch (err) {
            console.error('Eroare la asignare:', err);
            setError('Eroare la asignarea device-ului.');
        }
    };

    return (
        <div style={{ marginTop: '40px' }}>
            <h3>Management Device-uri</h3>
            {error && <p style={{ color: 'red' }}>{error}</p>}

            <form onSubmit={handleCreateDevice} style={{ marginBottom: '20px' }}>
                <h4>Adaugă un device nou</h4>
                <input
                    type="text"
                    placeholder="Nume"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="Descriere"
                    value={newDescription}
                    onChange={(e) => setNewDescription(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="Consum maxim"
                    value={newMaxConsumption}
                    onChange={(e) => setNewMaxConsumption(e.target.value)}
                    required
                />
                <button type="submit" disabled={loading}>
                    {loading ? 'Se creeaza...' : 'Creează Device'}
                </button>
            </form>

            <table border="1" style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nume</th>
                        <th>Descriere</th>
                        <th>Consum maxim</th>
                        <th>User ID</th>
                        <th>Acțiuni</th>
                    </tr>
                </thead>
                <tbody>
                    {devices.length === 0 ? (
                        <tr>
                            <td colSpan="6" style={{ textAlign: 'center' }}>
                                Nu exista device-uri
                            </td>
                        </tr>
                    ) : (
                        devices.map((d) => (
                            <tr key={d.id}>
                                <td>{d.id}</td>
                                <td>{d.name}</td>
                                <td>{d.description}</td>
                                <td>{d.maxConsumption}</td>
                                <td>{d.userId ?? '-'}</td>
                                <td>
                                    <button onClick={() => handleAssignDevice(d.id)}>
                                        Asigneaza
                                    </button>
                                    <button
                                        onClick={() => handleDeleteDevice(d.id)}
                                        style={{ marginLeft: '5px' }}
                                    >
                                        Sterge
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default DeviceManagement;

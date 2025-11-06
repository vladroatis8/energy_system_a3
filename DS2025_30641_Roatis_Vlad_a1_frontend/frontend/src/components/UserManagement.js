import React, { useState, useEffect } from 'react';
import api from '../api';

function UserManagement() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false); // Nou: indicator de încărcare

    const [newUsername, setNewUsername] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [newRole, setNewRole] = useState('CLIENT');

    const fetchUsers = async () => {
        try {
            const response = await api.get('/users');
            setUsers(response.data);
            setError(''); // Curățăm erorile anterioare
        } catch (err) {
            console.error('Eroare fetch users:', err);
            setError('Nu am putut încărca utilizatorii.');
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleDeleteUser = async (userId) => {
        if (window.confirm('Ești sigur că vrei să ștergi acest utilizator?')) {
            try {
                setLoading(true);
                await api.delete(`/users/${userId}`);
                await fetchUsers();
                setLoading(false);
            } catch (err) {
                console.error('Eroare delete:', err);
                setError('Eroare la ștergerea utilizatorului.');
                setLoading(false);
            }
        }
    };

    const handleCreateUser = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            // Trimitem cererea de creare
            const response = await api.post('/users', {
                username: newUsername,
                password: newPassword,
                role: newRole
            });

            console.log('User creat cu succes:', response.data);

            // Golim formularul
            setNewUsername('');
            setNewPassword('');
            setNewRole('CLIENT');

            // Așteptăm puțin pentru ca backend-ul să finalizeze complet
            // (inclusiv sincronizarea cu auth-service)
            await new Promise(resolve => setTimeout(resolve, 500));

            // Reîncărcăm lista de useri
            await fetchUsers();
            
            setLoading(false);
            
        } catch (err) {
            console.error('Eroare la creare:', err);
            setLoading(false);

            if (err.response) {
                const errorMsg = err.response.data;
                const statusCode = err.response.status;
                
                console.log('Status code:', statusCode);
                console.log('Error message:', errorMsg);
                
                // Gestionăm diferitele tipuri de erori
                if (typeof errorMsg === 'string') {
                    if (statusCode === 400) {
                        // Eroare de validare (username duplicat în user_db)
                        setError(errorMsg);
                    } else if (statusCode === 409) {
                        // Conflict (username există în auth_db)
                        setError(errorMsg + '\n\nSoluție: Șterge user-ul din auth_db sau folosește alt username.');
                    } else if (statusCode === 503) {
                        // Auth-service offline
                        setError(errorMsg + '\n\nVerifică în Docker dacă auth-service rulează: docker ps');
                    } else {
                        setError(errorMsg);
                    }
                } else {
                    setError('Eroare la crearea utilizatorului.');
                }
            } else if (err.request) {
                setError('Nu s-a primit răspuns de la server. Verifică dacă Docker rulează.');
            } else {
                setError('Eroare la trimiterea cererii: ' + err.message);
            }
        }
    };

    return (
        <div>
            <h3>Management Utilizatori</h3>
            
            {loading && (
                <p style={{color: 'blue', fontWeight: 'bold'}}>
                    Se procesează cererea...
                </p>
            )}
            
            {error && <p style={{color: 'red', fontWeight: 'bold'}}>{error}</p>}

            <form 
                onSubmit={handleCreateUser} 
                style={{ 
                    marginBottom: '20px', 
                    border: '1px solid #ccc', 
                    padding: '10px',
                    backgroundColor: '#f9f9f9'
                }}
            >
                <h4>Creare Utilizator Nou</h4>
                <div style={{marginBottom: '10px'}}>
                    <label>Username: </label>
                    <input
                        type="text"
                        value={newUsername}
                        onChange={(e) => setNewUsername(e.target.value)}
                        required
                        disabled={loading}
                        style={{marginLeft: '10px', padding: '5px'}}
                    />
                </div>
                <div style={{marginBottom: '10px'}}>
                    <label>Parolă: </label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        disabled={loading}
                        style={{marginLeft: '10px', padding: '5px'}}
                    />
                </div>
                <div style={{marginBottom: '10px'}}>
                    <label>Rol: </label>
                    <select 
                        value={newRole} 
                        onChange={(e) => setNewRole(e.target.value)}
                        disabled={loading}
                        style={{marginLeft: '10px', padding: '5px'}}
                    >
                        <option value="CLIENT">CLIENT</option>
                        <option value="ADMINISTRATOR">ADMINISTRATOR</option>
                    </select>
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Se creează...' : 'Crează Utilizator'}
                </button>
            </form>

            <table border="1" style={{ width: '100%', marginTop: '20px', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{backgroundColor: '#f0f0f0'}}>
                        <th style={{padding: '10px'}}>ID</th>
                        <th style={{padding: '10px'}}>Username</th>
                        <th style={{padding: '10px'}}>Rol</th>
                        <th style={{padding: '10px'}}>Acțiuni</th>
                    </tr>
                </thead>
                <tbody>
                    {users.length === 0 ? (
                        <tr>
                            <td colSpan="4" style={{textAlign: 'center', padding: '20px'}}>
                                Nu există utilizatori
                            </td>
                        </tr>
                    ) : (
                        users.map(user => (
                            <tr key={user.id}>
                                <td style={{padding: '8px', textAlign: 'center'}}>{user.id}</td>
                                <td style={{padding: '8px'}}>{user.username}</td>
                                <td style={{padding: '8px'}}>{user.role}</td>
                                <td style={{padding: '8px', textAlign: 'center'}}>
                                    <button
                                        onClick={() => handleDeleteUser(user.id)}
                                        disabled={loading}
                                        style={{
                                            marginRight: '5px',
                                            padding: '5px 10px',
                                            cursor: loading ? 'not-allowed' : 'pointer'
                                        }}
                                    >
                                        Șterge
                                    </button>
                                    <button
                                        disabled={loading}
                                        style={{
                                            padding: '5px 10px',
                                            cursor: loading ? 'not-allowed' : 'pointer'
                                        }}
                                    >
                                        Editează
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

export default UserManagement;
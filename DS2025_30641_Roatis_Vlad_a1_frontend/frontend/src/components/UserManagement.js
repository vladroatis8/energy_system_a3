import React, { useState, useEffect } from 'react';
import api from '../api';

function UserManagement() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [editUser, setEditUser] = useState(null);

  const [newUsername, setNewUsername] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newRole, setNewRole] = useState('CLIENT');

  // ========================= FETCH =========================
  const fetchUsers = async () => {
    try {
      const response = await api.get('/users');
      setUsers(response.data);
      setError('');
    } catch (err) {
      console.error('Eroare fetch users:', err);
      setError('Nu am putut incarca utilizatorii.');
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // ========================= DELETE =========================
  const handleDeleteUser = async (userId) => {
    if (window.confirm('Esti sigur ca vrei sa stergi acest utilizator?')) {
      try {
        setLoading(true);
        await api.delete(`/users/${userId}`);
        await fetchUsers();
      } catch (err) {
        console.error('Eroare delete:', err);
        setError('Eroare la stergerea utilizatorului.');
      } finally {
        setLoading(false);
      }
    }
  };

  // ========================= CREATE =========================
  const handleCreateUser = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/users', {
        username: newUsername,
        password: newPassword,
        role: newRole,
      });

      console.log('✅ User creat cu succes:', response.data);

      setNewUsername('');
      setNewPassword('');
      setNewRole('CLIENT');

      await fetchUsers();
    } catch (err) {
      console.error('Eroare la creare:', err);
      if (err.response) setError(err.response.data || 'Eroare la creare.');
      else setError('Eroare la comunicarea cu serverul.');
    } finally {
      setLoading(false);
    }
  };

  // ========================= EDIT =========================
  const handleEditClick = (user) => {
    setEditUser(user);
    setNewUsername(user.username);
    setNewRole(user.role);
    setNewPassword('');
  };

  // ========================= UPDATE =========================
  const handleUpdateUser = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await api.put(`/users/${editUser.id}`, {
        username: newUsername,
        password: newPassword || undefined,
        role: newRole,
      });

      console.log('✅ User actualizat cu succes!');
      setEditUser(null);
      setNewUsername('');
      setNewPassword('');
      setNewRole('CLIENT');

      await fetchUsers();
    } catch (err) {
      console.error('Eroare la actualizare:', err);
      setError('Eroare la actualizarea utilizatorului.');
    } finally {
      setLoading(false);
    }
  };

  // ========================= CANCEL =========================
  const handleCancelEdit = () => {
    setEditUser(null);
    setNewUsername('');
    setNewPassword('');
    setNewRole('CLIENT');
  };

  // ========================= RENDER =========================
  return (
    <div>
      <h3>Management Utilizatori</h3>

      {loading && <p style={{ color: 'blue', fontWeight: 'bold' }}>Se procesează cererea...</p>}
      {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}

      <form
        onSubmit={editUser ? handleUpdateUser : handleCreateUser}
        style={{
          marginBottom: '20px',
          border: '1px solid #ccc',
          padding: '10px',
          backgroundColor: '#f9f9f9',
        }}
      >
        <h4>{editUser ? 'Editare Utilizator' : 'Creare Utilizator Nou'}</h4>

        <div style={{ marginBottom: '10px' }}>
          <label>Username: </label>
          <input
            type="text"
            value={newUsername}
            onChange={(e) => setNewUsername(e.target.value)}
            required
            disabled={loading}
            style={{ marginLeft: '10px', padding: '5px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Parolă: </label>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder={editUser ? ' ' : ''}
            disabled={loading}
            style={{ marginLeft: '10px', padding: '5px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Rol: </label>
          <select
            value={newRole}
            onChange={(e) => setNewRole(e.target.value)}
            disabled={loading}
            style={{ marginLeft: '10px', padding: '5px' }}
          >
            <option value="CLIENT">CLIENT</option>
            <option value="ADMINISTRATOR">ADMINISTRATOR</option>
          </select>
        </div>

        {/* butonul  */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            gap: '10px',
            marginTop: '10px',
          }}
        >
          <button
            type="submit"
            disabled={loading}
            style={{
              padding: '8px 15px',
              backgroundColor: '#4CAF50',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontWeight: 'bold',
              transition: '0.2s',
            }}
          >
            {loading
              ? 'Se proceseaza...'
              : editUser
              ? ' Salveaza Modificările'
              : ' Creaza Utilizator'}
          </button>

          {editUser && (
            <button
              type="button"
              onClick={handleCancelEdit}
              disabled={loading}
              style={{
                padding: '8px 15px',
                backgroundColor: '#ddd',
                border: '1px solid #ccc',
                borderRadius: '5px',
                cursor: loading ? 'not-allowed' : 'pointer',
              }}
            >
              ❌ Anulează
            </button>
          )}
        </div>
      </form>

      <table
        border="1"
        style={{ width: '100%', marginTop: '20px', borderCollapse: 'collapse' }}
      >
        <thead>
          <tr style={{ backgroundColor: '#f0f0f0' }}>
            <th style={{ padding: '10px' }}>ID</th>
            <th style={{ padding: '10px' }}>Username</th>
            <th style={{ padding: '10px' }}>Rol</th>
            <th style={{ padding: '10px' }}>Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          {users.length === 0 ? (
            <tr>
              <td colSpan="4" style={{ textAlign: 'center', padding: '20px' }}>
                Nu exista utilizatori
              </td>
            </tr>
          ) : (
            users.map((user) => (
              <tr key={user.id}>
                <td style={{ padding: '8px', textAlign: 'center' }}>{user.id}</td>
                <td style={{ padding: '8px' }}>{user.username}</td>
                <td style={{ padding: '8px' }}>{user.role}</td>
                <td style={{ padding: '8px', textAlign: 'center' }}>
                  <button
                    onClick={() => handleEditClick(user)}
                    disabled={loading}
                    style={{
                      marginRight: '5px',
                      padding: '5px 10px',
                      cursor: loading ? 'not-allowed' : 'pointer',
                    }}
                  >
                    Editeaza
                  </button>

                  <button
                    onClick={() => handleDeleteUser(user.id)}
                    disabled={loading}
                    style={{
                      padding: '5px 10px',
                      cursor: loading ? 'not-allowed' : 'pointer',
                    }}
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

export default UserManagement;

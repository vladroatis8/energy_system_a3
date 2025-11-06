import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; // Importăm biblioteca instalată

// Creăm o instanță "axios" personalizată
const api = axios.create({
    baseURL: 'http://localhost' // Adresa de bază a backend-ului (Traefik)
});

// Acesta este un "interceptor" - rulează înainte de FIECARE cerere
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            // Atașăm token-ul la header
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// --- FUNCȚIA PE CARE AI UITAT S-O IMPORȚI ---
// Funcție helper pentru a obține datele din token-ul salvat
export const getAuthData = () => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        const decodedToken = jwtDecode(token);
        // Ne asigurăm că token-ul are datele și nu e expirat
        if (decodedToken.exp * 1000 < Date.now()) {
            localStorage.removeItem('token'); // Curățăm token-ul expirat
            localStorage.removeItem('role');
            return null;
        }
        return {
            userId: decodedToken.userId,
            role: decodedToken.role
        };
    } catch (e) {
        console.error('Token invalid:', e);
        return null; // Token-ul e invalid
    }
};

export default api;
import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; 

const api = axios.create({
    baseURL: 'http://localhost' 
});


api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
           
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);


export const getAuthData = () => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        const decodedToken = jwtDecode(token);

        if (decodedToken.exp * 1000 < Date.now()) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            return null;
        }

        return {
            userId: decodedToken.userId,
            role: decodedToken.role,
            token: token   
        };
    } catch (e) {
        console.error('Token invalid:', e);
        return null;
    }
};

export async function getDeviceDailyConsumption(deviceId, date) {
    const response = await api.get(`/api/consumption/device/${deviceId}?date=${date}`);
    return response.data;
}


export default api;
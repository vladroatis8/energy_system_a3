import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import {
    Chart as ChartJS,
    LineElement,
    CategoryScale,
    LinearScale,
    PointElement,
    Tooltip,
    Legend
} from "chart.js";
import { getDeviceDailyConsumption, getAuthData } from "../api";

ChartJS.register(LineElement, CategoryScale, LinearScale, PointElement, Tooltip, Legend);

function ClientConsumption({ deviceId }) {
    const [date, setDate] = useState(new Date().toISOString().substring(0, 10));
    const [data, setData] = useState([]);
    const authData = getAuthData();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await getDeviceDailyConsumption(deviceId, date);
                console.log("Date consum:", response);
                setData(response);
            } catch (err) {
                console.error("Eroare la preluarea consumului:", err);
            }
        };

        if (deviceId && authData?.token) {
            fetchData();
        }
    }, [deviceId, date, authData]);

    const chartData = {
        labels: data.map(d => d.hour),
        datasets: [
            {
                label: `Consum în kWh`,
                data: data.map(d => d.total),
                borderColor: "rgb(75, 192, 192)",
                backgroundColor: "rgba(75, 192, 192, 0.4)",
                tension: 0.2
            }
        ]
    };

    return (
        <div style={{ marginTop: "20px" }}>
            <label>Selecteaza ziua:</label>
            <input
                type="date"
                value={date}
                onChange={e => setDate(e.target.value)}
                style={{ marginLeft: "10px" }}
            />

            <div style={{ width: "800px", marginTop: "20px" }}>
                {data.length === 0 ? (
                    <p>Nu exista date pentru aceasta zi.</p>
                ) : (
                    <Line data={chartData} />
                )}
            </div>
        </div>
    );
}

export default ClientConsumption;

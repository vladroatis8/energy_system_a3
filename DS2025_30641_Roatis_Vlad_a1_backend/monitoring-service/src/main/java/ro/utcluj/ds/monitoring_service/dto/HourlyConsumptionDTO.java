package ro.utcluj.ds.monitoring_service.dto;

public class HourlyConsumptionDTO {

    private int hour;
    private double total;

    public HourlyConsumptionDTO(int hour, double total) {
        this.hour = hour;
        this.total = total;
    }

    public int getHour() { return hour; }
    public double getTotal() { return total; }
    
}

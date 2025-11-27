package ro.utcluj.ds.device_service.messaging;

public class DeviceCreatedEvent {

    private Long id;
    private String name;
    private Double maxConsumption;

    public DeviceCreatedEvent() {
        // necesar pentru deserializare JSON
    }

    public DeviceCreatedEvent(Long id, String name, Double maxConsumption) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }
}

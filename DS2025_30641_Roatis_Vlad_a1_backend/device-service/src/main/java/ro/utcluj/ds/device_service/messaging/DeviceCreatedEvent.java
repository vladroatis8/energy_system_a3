package ro.utcluj.ds.device_service.messaging;

public class DeviceCreatedEvent {

    private String eventType = "CREATED";
    private Long id;
    private String name;
    private Double maxConsumption;

    public DeviceCreatedEvent() {}

    public DeviceCreatedEvent(Long id, String name, Double maxConsumption) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
    }

    // Getters & Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
}
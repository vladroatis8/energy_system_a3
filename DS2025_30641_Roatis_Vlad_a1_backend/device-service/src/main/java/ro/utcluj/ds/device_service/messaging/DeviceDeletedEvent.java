package ro.utcluj.ds.device_service.messaging;

public class DeviceDeletedEvent {

    private String eventType = "DELETED";
    private Long id;

    public DeviceDeletedEvent() {}

    public DeviceDeletedEvent(Long id) {
        this.id = id;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
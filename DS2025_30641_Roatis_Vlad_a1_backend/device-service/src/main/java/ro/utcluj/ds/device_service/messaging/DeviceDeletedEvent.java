package ro.utcluj.ds.device_service.messaging;

public class DeviceDeletedEvent {

    private Long id;

    public DeviceDeletedEvent() {
        // necesar pentru deserializare JSON
    }

    public DeviceDeletedEvent(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

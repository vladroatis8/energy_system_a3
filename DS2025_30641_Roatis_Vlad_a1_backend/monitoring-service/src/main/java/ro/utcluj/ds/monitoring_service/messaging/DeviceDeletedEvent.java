package ro.utcluj.ds.monitoring_service.messaging;

public class DeviceDeletedEvent {

    private Long id;

    public DeviceDeletedEvent() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

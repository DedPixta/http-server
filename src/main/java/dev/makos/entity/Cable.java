package dev.makos.entity;

public class Cable {
    private long id;
    private String name;
    private long deviceAId;
    private long deviceZId;
    private long length;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDeviceAId() {
        return deviceAId;
    }

    public void setDeviceAId(long deviceAId) {
        this.deviceAId = deviceAId;
    }

    public long getDeviceZId() {
        return deviceZId;
    }

    public void setDeviceZId(long deviceZId) {
        this.deviceZId = deviceZId;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}

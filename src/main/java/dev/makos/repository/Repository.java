package dev.makos.repository;

import dev.makos.entity.Cable;
import dev.makos.entity.Device;
import dev.makos.entity.Port;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Repository {
    private static AtomicLong deviceIdSequence = new AtomicLong(0L);
    private static AtomicLong portIdSequence = new AtomicLong(0L);
    private static AtomicLong cableIdSequence = new AtomicLong(0L);

    private static Map<Long, Device> devices = new ConcurrentHashMap<>();
    private static Map<Long, Cable> cables = new ConcurrentHashMap<>();

    public static Device addDevice(Device device) {
        device.setId(deviceIdSequence.incrementAndGet());

        return devices.put(device.getId(), device);
    }

    public static Port addDevicePort(long deviceId, Port port) {
        Device device = devices.get(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("Device with ID = " + deviceId + "is not found!");
        }
        port.setId(portIdSequence.incrementAndGet());

        device.addPort(port);

        return port;
    }


    public static Device fetchDevice(long deviceId) {
        Device device = devices.get(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("Device with ID = " + deviceId + "is not found!");
        }

        return device;
    }

    public static List<Device> fetchAllDevices() {
        return List.copyOf(devices.values());
    }

    public static List<Port> fetchAllPortsByDevice(long deviceId) {
        Device device = devices.get(deviceId);
        if(device == null) {
            throw new IllegalArgumentException("Device with ID = " + deviceId + "is not found!");
        }

        return List.copyOf(device.getPorts());
    }

    public static Device removeDevice(long deviceId) {
        return devices.remove(deviceId);
    }

    public static Cable addCable(Cable cable) {
        if (!devices.containsKey(cable.getDeviceAId())){
            throw new IllegalArgumentException("Device with DeviceAId = " + cable.getDeviceAId() + "is not found!");
        }

        if (!devices.containsKey(cable.getDeviceZId())){
            throw new IllegalArgumentException("Device with DeviceZId = " + cable.getDeviceZId() + "is not found!");
        }

        cable.setId(cableIdSequence.incrementAndGet());
        return cables.put(cable.getId(), cable);
    }

    public static List<Cable> fetchAllCables() {
        return List.copyOf(cables.values());
    }

    public static Cable fetchCable(long cableId) {
        Cable cable = cables.get(cableId);
        if(cable == null) {
            throw new IllegalArgumentException("Cable with ID = " + cableId + "is not found!");
        }

        return cable;
    }
}

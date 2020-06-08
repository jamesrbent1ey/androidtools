package app.bentleyis.pluginprovider.model;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Capabilities define the capabilities of the Host and the Plugin. The serialized (toString) form
 * of this object is passed between the plug in and Host via the getCapabilities method in the appropriate
 * aidl file.
 *
 * Capabilities defines interfaces (methods) and telemetry (events emitted) for both the plug-in and
 * host. Telemetry is passed through the notify method of the appropriate aidl interface.
 * Telemetry may not be filtered. The implementation should account for reception of all defined
 * Telemetry values.
 *
 * JSON is the serialization form used in this implementation.
 */
public class Capabilities extends CapabilityBase {
    String version;
    LinkedList<CapabilityInterface> interfaces = new LinkedList<>();
    LinkedList<CapabilityTelemetry> telemetries = new LinkedList<>();

    /**
     * Build Capabilities from JSON string.
     * @param json
     * @return
     */
    public static Capabilities fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Capabilities.class);
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Collection<CapabilityInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Collection<CapabilityInterface> interfaces) {
        this.interfaces = new LinkedList<>(interfaces);
    }

    public Collection<CapabilityTelemetry> getTelemetries() {
        return telemetries;
    }

    public void setTelemetries(Collection<CapabilityTelemetry> telemetries) {
        this.telemetries = new LinkedList<>(telemetries);
    }

    public synchronized void addTelemetry(CapabilityTelemetry telemetry) {
        if(telemetries.contains(telemetry)) {
            return;
        }
        telemetries.add(telemetry);
    }

    public synchronized void addInterface(CapabilityInterface iface) {
        if(interfaces.contains(iface)) {
            return;
        }
        interfaces.add(iface);
    }

    public synchronized boolean removeTelemetry(CapabilityTelemetry telemetry) {
        return telemetries.remove(telemetry);
    }

    public synchronized boolean removeInterface(CapabilityInterface iface) {
        return interfaces.remove(iface);
    }
}

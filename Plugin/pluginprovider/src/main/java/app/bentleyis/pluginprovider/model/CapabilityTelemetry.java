package app.bentleyis.pluginprovider.model;

public class CapabilityTelemetry extends CapabilityBase {
    final String type = "interface";
    CapabilityValue value;

    public String getType() {
        return type;
    }

    public CapabilityValue getValue() {
        return value;
    }

    public void setValue(CapabilityValue value) {
        this.value = value;
    }
}

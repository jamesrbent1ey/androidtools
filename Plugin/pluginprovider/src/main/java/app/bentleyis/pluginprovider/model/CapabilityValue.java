package app.bentleyis.pluginprovider.model;

public class CapabilityValue extends CapabilityBase {
    CapabilityType type;
    String value;

    public CapabilityType getType() {
        return type;
    }

    public void setType(CapabilityType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

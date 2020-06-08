package app.bentleyis.pluginprovider.model;

import java.util.Collection;
import java.util.LinkedList;

public class CapabilityInterface extends CapabilityBase {
    final String type = "interface";
    LinkedList<CapabilityValue> parameters = new LinkedList<>();
    CapabilityValue response;

    public String getType() {
        return type;
    }

    public Collection<CapabilityValue> getParameters() {
        return parameters;
    }

    public synchronized void addParameter(CapabilityValue parameter) {
        if(parameters.contains(parameter)) {
            return;
        }
        parameters.add(parameter);
    }

    public CapabilityValue getResponse() {
        return response;
    }

    public void setResponse(CapabilityValue response) {
        this.response = response;
    }
}

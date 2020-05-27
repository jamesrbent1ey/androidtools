// IotServiceAidl.aidl
package app.bentleyis.azureiot;

// Declare any non-default types here with import statements
import app.bentleyis.azureiot.IotServiceListenerAidl;

interface IotServiceAidl {
    void setListener(in IotServiceListenerAidl listener);
    void sendMessage(in String type, in byte[] payload);
}

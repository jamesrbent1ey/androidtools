// IotServiceListenerAidl.aidl
package app.bentleyis.azureiot;

// Declare any non-default types here with import statements

interface IotServiceListenerAidl {
   void callMethod(in String name, in byte[] payload);
   void receiveMessage(in String json);
}

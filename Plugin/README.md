# Plugin framework for Android
This project provides a dynamic plug-in framework for android. 

Originally, this was developed to support subscriptions prior to full support in google play. Each
subscription would be a separate, purchasable, application. The application that uses the subscription
would discover it, enabling features accordingly

Each plug-in provider implements a BroadcastReceiver registered to receive broadcast events sent by
the plug-in host. Upon receiving such broadcast, and validating it, the provider would bind to the
Host to register. 

One Caveat to this system. Recent Android changes favor Jobs and WorkManager over BroadcastReceivers
and Bound services. Implementation for these will also be provided. Note that, for Android 8 (api 26) 
and higher, BroadcastReceivers cannot be declared in the manifest - use scheduled jobs.

# Executable extension
It is possible to extend and application with executable content. Javascript content can be transferred
to allow this extension. Note that this is a dangerous practice and transfer should be secure and
verifiable.

Additional alternatives are to stream layout files for visualization or create directives/scripts for 
execution by the host.

Plugins can also export their capabilities - i.e. method declarations - that the host can invoke and that
other plug-ins may reference.

for example, this project contains an implementation that passes JSON between Plugin and Host. The JSON
is structured similar to Azure IoT Direct Method as the format is well suited for executable plugins and
will provide a much cleaner migration path to a cloud based Host.
[Understand and invoke direct methods from IoT Hub](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-direct-methods)

POJOs can be created to wrap (marshall/demarshall) the JSON and provided a programmable interface for
simplicity. (similar to Command Pattern)

Capability definition examples can be found here [Azure IoT DTDL](https://github.com/Azure/IoTPlugandPlay/tree/master/DTDL)

# dependencies
'com.google.code.gson:gson:2.8.6'

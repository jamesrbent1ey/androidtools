# Azure IoT integration
The Azure IoT module provides integration with Azure IoT Hub. This module uses MessageBroker to bridge
IoT Hub communications to the Android application. The module uses MicroKernel to perform remote
method invocations initiated in the cloud.

Construct an Agent to establish bi-directional flow, through the MessageBroker, to the IoT Hub.
In the Agent, you may specify one or more topics to publish received messages to, as well as one or
more topics to receive messages for relay to the IoT Hub.

Device Twin Desired Properties are handled separately through the Agent. In Azure, Desired Properties represent
property settings (read-only) that are baseline (i.e. configuration settings). The Application may choose to 
apply these settings and update the Twin to reflect changes.  Properties, provided in the IotMessage, are
used by IotHub to update the Twin's properties.

There are 2 entry points into the system:  
- Agent establishes an IoT connection and registers to publish and receive Message objects from the MessageBroker. 
The DeviceMethodBridge uses the Microkernel Registry to find MethodHandler registrations allowing method 
invocations from the cloud. IotMessagePublisher publishes cloud messages to the MessageBroker while
MessageRelay receives Message objects from the MessageBroker, forwarding them to the cloud.
- IotService is a Bound Service that allows multiple applications send/receive messages to/from the Cloud.
IotService returns an IotServiceAidl binder which accepts an IotServiceListenerAidl. IotServiceAidl allows
applications to send messages, IotServiceListenerAidl allows applications to receive messages and method
calls. Create the service by calling bind with BIND_AUTO_CREATE specified.

See the appropriate class for configuration requirements.


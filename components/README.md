# Components 
Each component/module either provides an extension of another component or the base for implementing a solution (framework)

For instance, MessageBroker provides a light message broker for use implementing an event-based architecture in android/java applications.
Additional components/modules may utilize MessageBroker to provide some implemenation - such as connecting messaging to Azure services.

All modules - except app - create libraries for inclusion in other Android applications.

Module:
messagebroker - light message broker implementing Publish-Subscribe pattern/event-based
appcenter_sub - subscriber to log events to Visual Studio App Center
appinsights_sub - subscriber to log events to Azure Application Insights

Subscribers, such as appcenter_sub and appinsights_sub, currently have their own flavor of Message that they will accept.
You can change this such that more than one subscriber can receive a common message (i.e. JSON encoded). Take care
when doing so such that each subscriber can extract the necessary values from the message (i.e. use appropriate
data transformations).

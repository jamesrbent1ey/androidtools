# Visual Studio App Center - Subscriber
This module holds a MessageBroker Subscriber implementation, used to connect Android applications to
App Center.

Use App Center for globally distributed mobile applications of various types (i.e. Android and iOS and windows)
App Center provides monitoring of mobile devices, storage for logged data and default views of the data.
The data can then be exported to Azure App Insights for more control over storage and more analytics options.


Construct an instance of AppCenterSubscriber to initialize. This will create and register the subscriber
with the MessageBroker. To initialize, the construct must be given the Application (required by App Center
SDK), the app center key and the topic to subscribe to.

Once created, simply create Metric objects and publish them to the correct topic, using the MessageBroker.

Messages will be posted to App Center in the background in accordance with App Center policies - this
is at least a 3 second delay even for Critical messages. App Center SDK stores messages and fowards
when connection/session established. App Center SDK does a random back-off for connection establishment, 
not modifiable. Further, the connection/read/write timeouts are not modifiable and sit at 10 seconds by
default.  App Center SDK does not work well with proxies that require authentication. Additional restrictions
and costs are covered by use of Visual Studio App Center and its Portal.

App Center SDK manages persistence of log data. It will transfer logs in compressed binary suitable for
more restrictive environments (such as mobile and embedded)

Check out AppInsightsSubscriber if you would like to publish directly to Azure

Sub-class Metric for other forms of event/metric logs as necessary. Modify the Subscriber's receive
method to handle the sub-classed object appropriately. 

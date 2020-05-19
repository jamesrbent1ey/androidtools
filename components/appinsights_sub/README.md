# Azure Application Insights Subscriber
This subscriber uses the MessageBroker to receive Metric objects. The Metric objects are logged to
Azure Application Insights.

Use Application insights for globally distributed applications of various types. This include mobile,
embedded and web applications. Application insights provides analytical view, integration with Azure and
various storage options. Application Insights provides the greatest flexibility for search, filter,
display and transfer management.

This subscriber uses Application Insights Java SDK which has queued log events - not persistent between
application instances. The application will have to maintain persistence and can control timing of when
logs are transferred to the cloud.  Application Insights transfers JSON encoded data which can be large
for embedded and mobile devices.

Sub-class Metric for other forms of event/metric logs as necessary. Modify the Subscriber's receive
method to handle the sub-classed object appropriately. 
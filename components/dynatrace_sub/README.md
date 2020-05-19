# Dynatrace Subscriber
This subscriber posts Metric objects to Dynatrace.

Dynatrace is typically used for Application Performance Management. This agent is optimized to produce
logs and events for consumption by Dynatrace tools. It is not a general purpose logger well suited for
cloud analytics.

This subscriber uses a single event Metric for a log. It is posted to Dynatrace as a single string.
Unlike App Center and Application Insights, back-end analytics need to interpret the contents of the 
string event for more detail.

Sub-class Metric for other forms of event logs/Actions as necessary. Modify the Subscriber's receive
method to handle the sub-classed object appropriately. 
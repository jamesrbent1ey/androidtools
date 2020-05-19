/*
 * Copyright (c) 2020  James Bentley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.bentleyis.appinsightssub;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;

import app.bentleyis.messagebroker.Message;
import app.bentleyis.messagebroker.MessageBroker;
import app.bentleyis.messagebroker.Subscriber;

public class AppInsightsSubscriber implements Subscriber {

    private final String m_instrumentationKey;
    private final String m_topic;
    private final String m_roleName;
    private TelemetryConfiguration m_configuration;
    private TelemetryClient m_client;
    private boolean m_flushOnReceive;

    /**
     * Construct with the instrumentation key and the topic to listen to for Metric objects
     * @param roleName session name for metrics logging
     * @param instrumentationKey
     * @param topic
     */
    public AppInsightsSubscriber(String roleName, String instrumentationKey, String topic) {
        m_roleName = roleName;
        m_instrumentationKey = instrumentationKey;
        m_topic = topic;
        initialize();
    }

    private void initialize() {
        m_configuration = TelemetryConfiguration.getActive();
        m_configuration.getChannel().setDeveloperMode(true); // verbose logging to console
        m_configuration.setInstrumentationKey(m_instrumentationKey);
        m_configuration.setTrackingIsDisabled(false);
        // can set role name here for more filtering options in app insights
        m_configuration.setRoleName(m_roleName);
        MessageBroker.getInstance().subscribe(m_topic,this);
    }

    @Override
    public void receive(Message message) {
        if(!(message instanceof Metric)) {
            return;
        }

        Metric metric = (Metric)message;
        m_client.trackEvent(metric.getId(),metric.getProperties(),metric.getMetrics());

        if(m_flushOnReceive) {
            m_client.flush();
        }

    }

    public void setFlushOnReceive(boolean state) {
        m_flushOnReceive = state;
    }

    /**
     * Flush queue to the cloud
     */
    public void flush() {
        m_client.flush();
    }
}

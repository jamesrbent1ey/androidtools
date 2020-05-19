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

package app.bentleyis.appcentersub;

import android.app.Application;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;

import app.bentleyis.messagebroker.Message;
import app.bentleyis.messagebroker.MessageBroker;
import app.bentleyis.messagebroker.Subscriber;

/**
 * The AppCenterSubscriber can receive Metrics through the MessageBroker
 * The Metrics are logged to Visual Studio App Center
 */
public class AppCenterSubscriber implements Subscriber {
    Application m_application;
    String m_key;
    String m_topic;

    /**
     * App Center requires the Android Application to initialize. Constructing an AppCenterSubscriber
     * will initialize the App Center SDK and register a subscriber on the provided topic
     * From there, publishes (of type Metric) will be received by AppCenterSubscriber and posted to
     * Visual Studio App Center.
     * @param application  Application to initialize on
     * @param key   App Center account key
     * @param topic  topic to receive Metric objects on
     */
    public AppCenterSubscriber(Application application, String key, String topic) {
        m_application = application;
        m_key = key;
        m_topic = topic;
        create();
    }

    private void create() {
        AppCenter.start(m_application, m_key, Analytics.class);
        MessageBroker.getInstance().subscribe(m_topic, this);
    }

    @Override
    public void receive(Message message) {
        if(!(message instanceof Metric)) {
            return;
        }

        Metric metric = (Metric) message;
        Analytics.trackEvent(metric.getId(), metric.getProperties(), metric.getFlags());
    }
}

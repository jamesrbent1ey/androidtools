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

package app.bentleyis.dynatracesub;

import android.app.Application;

import com.dynatrace.android.agent.DTXAction;
import com.dynatrace.android.agent.Dynatrace;
import com.dynatrace.android.agent.conf.DynatraceConfigurationBuilder;

import app.bentleyis.messagebroker.Message;
import app.bentleyis.messagebroker.MessageBroker;
import app.bentleyis.messagebroker.Subscriber;

/**
 * Dynatrace subscriber - custom events
 */
public class DynatraceSubscriber implements Subscriber {

    private final String m_applicationId;
    private final String m_beaconUrl;
    private final String m_topic;

    /**
     * Dynatrace captures detail from the Application Context. logs are posted based on beconUrl and
     * application Id.  This Subscriber will listen for Metrics on the given topic
     * @param application
     * @param applicationId
     * @param beaconUrl
     * @param topic
     */
    public DynatraceSubscriber(Application application, String applicationId, String beaconUrl, String topic) {
        m_applicationId = applicationId;
        m_beaconUrl = beaconUrl;
        m_topic = topic;
        initialize(application);
    }

    private void initialize(Application application) {
        Dynatrace.startup(application.getApplicationContext(),
            new DynatraceConfigurationBuilder(m_applicationId,m_beaconUrl)
                .buildConfiguration());
        MessageBroker.getInstance().subscribe(m_topic, this);
    }

    @Override
    public void receive(Message message) {
        if(!(message instanceof Metric)) {
            return;
        }
        Metric metric = (Metric) message;
        DTXAction action = Dynatrace.enterAction(metric.getId());
        action.reportEvent(metric.getEvent());
        action.leaveAction();
    }
}

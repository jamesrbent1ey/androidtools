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

package app.bentleyis.azureiot;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

import java.util.Map;

import app.bentleyis.messagebroker.Message;
import app.bentleyis.messagebroker.Subscriber;

/**
 * Relay Message object received from the MessageBroker, for a specific topic, to the cloud through
 * the IoT connection.
 */
public class MessageRelay implements Subscriber, IotHubEventCallback {
    Agent m_agent;

    public MessageRelay(Agent agent) {
        m_agent = agent;
    }

    @Override
    public void receive(Message message) {
        if(!(message instanceof IotMessage)) {
            return;
        }

        IotMessage m = (IotMessage) message;
        Map<String,String> properties = m.getProperties();
        com.microsoft.azure.sdk.iot.device.Message mout =
                new com.microsoft.azure.sdk.iot.device.Message(m.getPayload());
        for(String name: properties.keySet()) {
            mout.setProperty(name, properties.get(name));
        }
        DeviceClient client = m_agent.getClient();
        if(client != null) {
            client.sendEventAsync(mout,this,this);
        }
    }

    @Override
    public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
        // called when send completes
    }
}

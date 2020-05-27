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

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageProperty;

import java.util.List;

import app.bentleyis.messagebroker.MessageBroker;

/**
 * The IoT Message Publisher relays messages received from the cloud, to one or more topics registered
 * with the Agent.
 */
class IotMessagePublisher implements com.microsoft.azure.sdk.iot.device.MessageCallback {
    private final Agent m_agent;

    public IotMessagePublisher(Agent agent) {
        m_agent = agent;
    }

    @Override
    public IotHubMessageResult execute(Message message, Object callbackContext) {
        // Note that this is executing on the communication thread
        String id = message.getProperty("id");
        if(id == null) {
            id = "unk";
        }
        IotMessage msg = new IotMessage(id);

        MessageProperty[] msgProps = message.getProperties();
        for(int i = 0; msgProps != null && i < msgProps.length; i++) {
            msg.setProperty(msgProps[i].getName(),msgProps[i].getValue());
        }
        msg.setPayload(message.getBytes());

        List<String> topics = m_agent.getPublishTopics();
        if(topics.isEmpty()) {
            return IotHubMessageResult.ABANDON; // no one to receive
        }

        for(String topic: topics) {
            MessageBroker.getInstance().publish(topic, msg);
        }

        return IotHubMessageResult.COMPLETE;
    }
}

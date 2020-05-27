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

import android.support.annotation.Nullable;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.MessageCallback;
import com.microsoft.azure.sdk.iot.device.ProxySettings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;

import app.bentleyis.messagebroker.MessageBroker;

/**
 * The Agent establishes a connection with the IoT Hub.
 * The Agent then receives messages and  publishes them, via the MessageBroker,
 * to one or more topics. Similarly, the Agent can be subscribed to one or more topics and relay
 * message received to the IoT Hub.
 */
public class Agent implements IotHubEventCallback {
    private ProxySettings m_proxy;
    private SSLContext m_sslContext;
    DeviceClient m_client;
    private IotHubClientProtocol m_protocol;
    private String m_connectionString;
    private DeviceMethodBridge m_methodBridge;
    LinkedHashMap<String,MessageRelay> m_relays = new LinkedHashMap<>();
    private MessageCallback m_publisher;
    private List<String> m_publishTopics = new LinkedList<>();

    /**
     * The Agent must be constructed with the connection string and transport protocol and may be created
     * with SSLContext and/or ProxySettings
     * @param connectionString
     * @param protocol
     */
    public Agent(String connectionString, IotHubClientProtocol protocol,
                 @Nullable ProxySettings proxy, @Nullable SSLContext sslContext) {
        m_connectionString = connectionString;
        m_protocol = protocol;
        m_proxy = proxy;
        m_sslContext = sslContext;

        initialize();
    }

    private void initialize() {
        try {
            m_client = null;
            if(m_sslContext == null) {
                m_client = new DeviceClient(m_connectionString, m_protocol);
            } else {
                m_client = new DeviceClient(m_connectionString,m_protocol,m_sslContext);
            }

            // TODO this is on the network - needs to be executed in background thread
            if(m_proxy != null) {
                m_client.setProxySettings(m_proxy);
            }
            m_client.open();
            m_methodBridge = new DeviceMethodBridge();

            // subscribe the bridge to the client to handle mapping calls to actual registered class methods
            // status is handled in this agent
            m_client.subscribeToDeviceMethod(m_methodBridge, this,
                    this, this);

            m_publisher = new IotMessagePublisher(this);
            m_client.setMessageCallback(m_publisher, this);

            //TODO m_client.startDeviceTwin();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specify a topic the Agent should relay messages to
     * @param topic
     */
    public synchronized void relayMessagesFrom(String topic) {
        if(m_relays.containsKey(topic)) {
            return;
        }
        MessageRelay relay = new MessageRelay(this);
        m_relays.put(topic, relay);
        MessageBroker.getInstance().subscribe(topic,relay);
    }

    /**
     * Stop a message relay to a topic
     * @param topic
     */
    public synchronized void stopRelayFrom(String topic) {
        MessageRelay relay = m_relays.get(topic);
        if(relay == null) {
            return;
        }
        MessageBroker.getInstance().unsubscribe(topic,relay);
        m_relays.remove(topic);
    }

    @Override
    public void execute(IotHubStatusCode responseStatus, Object callbackContext) {

    }

    public DeviceClient getClient() {
        return m_client;
    }

    public List<String> getPublishTopics() {
        return m_publishTopics;
    }

    /**
     * Publish received messages to a specific topic
     * @param topic
     */
    public synchronized void publishTo(String topic) {
        if(m_publishTopics.contains(topic)) {
            return;
        }
        m_publishTopics.add(topic);
    }

    /**
     * Stop publishing received messages to a topic
     * @param topic
     */
    public synchronized void stopPublishTo(String topic) {
        m_publishTopics.remove(topic);
    }

}

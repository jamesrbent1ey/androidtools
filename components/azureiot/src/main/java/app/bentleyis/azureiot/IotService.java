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

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;
import com.microsoft.azure.sdk.iot.device.MessageProperty;
import com.microsoft.azure.sdk.iot.device.ProxySettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.net.ssl.SSLContext;

/**
 * The IotService allows for a single Bound Service to maintain an IoT Hub connection for multiple
 * applications. Simply bind with BIND_AUTO_CREATE flag set to establish connection. To support multiple
 * applications, an application must call startService then bind.
 *
 * The returned Binder is IotServiceAidl which allows for sending messages to the cloud. It also allows
 * registration of an IotServiceListenerAidl for receiving messages, and executing methods, from the
 * cloud.
 */
public class IotService extends Service implements MessageCallback, DeviceMethodCallback, IotHubEventCallback {
    public static final String PROXY_PORT = "port";
    public static final String PROXY_HOST = "host";
    public static final String PROXY_USER = "user";
    public static final String PROXY_PASSWORD = "password";
    public static final String CONNECTION_STRING = "connString";
    public static final String PROTOCOL = "protocol";
    public static final String IOT_CONNECTION_BUNDLE_NAME = "iot_connection";
    public static final String PROXY_BUNDLE_NAME = "proxy";
    LinkedHashMap<Intent,IotBinder> m_bindings = new LinkedHashMap<>();
    LinkedHashMap<Intent,IotBinder> m_unbound = new LinkedHashMap<>();

    private IotHubClientProtocol m_protocol = IotHubClientProtocol.MQTT;
    private String m_connectionString;
    private ProxySettings m_proxy;
    private SSLContext m_sslContext;
    DeviceClient m_client;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize(intent);
        // based on new guidelines, there is a window in which this service may execute, without
        // active bindings, before the OS will stop the service. Calling startService will allow
        // this service to behave just like a bound service, but for more than one application.
        return START_STICKY;
    }

    @Override
    public synchronized IBinder onBind(Intent intent) {
        initialize(intent);
        IotBinder binder = new IotBinder(this);
        m_bindings.put(intent,binder);
        return binder;
    }

    private void getProxyFrom(Intent intent) {
        String user = null;
        String password = null;
        String host = null;
        int port = -1;
        Bundle bundle = intent.getBundleExtra(PROXY_BUNDLE_NAME);
        if(bundle == null) {
            return; // no bundle
        }
        user = bundle.getString(PROXY_USER);
        password = bundle.getString(PROXY_PASSWORD);
        host = bundle.getString(PROXY_HOST);
        port = bundle.getInt(PROXY_PORT);
        constructProxy(user, password, host, port);
    }

    private void constructProxy(String user, String password, String host, int port) {
        if(user == null || password == null || host == null) {
            return;
        }
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port));
        m_proxy = new ProxySettings(proxy, user, password.toCharArray());
    }

    private void getProxyFrom(ServiceInfo si) {
        if(si == null || si.metaData == null) {
            return;
        }
        String user = si.metaData.getString(PROXY_USER);
        String password = si.metaData.getString(PROXY_PASSWORD);
        String host = si.metaData.getString(PROXY_HOST);
        int port = si.metaData.getInt(PROXY_PORT);
        constructProxy(user,password,host,port);
    }

    private void getConnectionInfoFrom(ServiceInfo si) {
        if(si == null || si.metaData == null) {
            return;
        }
        String connectionString = si.metaData.getString(CONNECTION_STRING);
        String protocol = si.metaData.getString(PROTOCOL);
        if(connectionString != null) {
            m_connectionString = connectionString;
        }
        if(protocol != null) {
            m_protocol = mapToProtocol(protocol);
        }
    }

    private IotHubClientProtocol mapToProtocol(String protocol) {
        if(protocol.equalsIgnoreCase(IotHubClientProtocol.AMQPS.toString())) {
            return IotHubClientProtocol.AMQPS;
        }
        if(protocol.equalsIgnoreCase(IotHubClientProtocol.AMQPS_WS.toString())) {
            return IotHubClientProtocol.AMQPS_WS;
        }
        if(protocol.equalsIgnoreCase(IotHubClientProtocol.MQTT_WS.toString())) {
            return IotHubClientProtocol.MQTT_WS;
        }
        return IotHubClientProtocol.MQTT;
    }

    private void getConnectionInfoFrom(Intent intent) {
        Bundle bundle = intent.getBundleExtra(IOT_CONNECTION_BUNDLE_NAME);
        if(bundle == null) {
            return; // no bundle
        }
        String connectionString = bundle.getString(CONNECTION_STRING);
        String protocol = bundle.getString(PROTOCOL);
        if(connectionString != null) {
            m_connectionString = connectionString;
        }
        if(protocol != null) {
            m_protocol = mapToProtocol(protocol);
        }
    }

    private void getSettings(Intent intent) {
        getProxyFrom(intent);

        ServiceInfo serviceInfo = null;
        try {
            serviceInfo = getPackageManager().getServiceInfo(
                    new ComponentName(this, this.getClass()),
                    PackageManager.GET_META_DATA
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getProxyFrom(serviceInfo);

        getConnectionInfoFrom(intent);
        getConnectionInfoFrom(serviceInfo);
    }

    private synchronized void initialize(Intent intent) {
        if(m_client != null) {
            return;
        }

        // get proxy settings and/or context settings
        getSettings(intent);

        // now connect
        try {
            if (m_sslContext == null) {
                m_client = new DeviceClient(m_connectionString, m_protocol);
            } else {
                m_client = new DeviceClient(m_connectionString, m_protocol, m_sslContext);
            }

            if (m_proxy != null) {
                m_client.setProxySettings(m_proxy);
            }
            m_client.open();

            // subscribe the bridge to the client to handle mapping calls to actual registered class methods
            // status is handled in this agent
            m_client.subscribeToDeviceMethod(this, this,
                    this, this);

            m_client.setMessageCallback(this, this);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void onDestroy() {
        // this is now called when the service becomes idle (no more bindings from any application)
        if(m_client != null) {
            try {
                m_client.closeNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        m_client = null;
        m_bindings.clear();
        m_unbound.clear();
        super.onDestroy();
    }

    @Override
    public synchronized boolean onUnbind(Intent intent) {
        if(m_bindings.containsKey(intent)) {
            m_unbound.put(intent,m_bindings.remove(intent));
        }
        return true;
    }

    @Override
    public synchronized void onRebind(Intent intent) {
        if(m_unbound.containsKey(intent)) {
            m_bindings.put(intent,m_unbound.remove(intent));
        }
        super.onRebind(intent);
    }

    public void sendMessage(String type, byte[] payload) {
        if(m_client == null) {
            System.err.println(getClass().getSimpleName()+" no client");
            return;
        }
        Message msg = new Message(payload);
        msg.setProperty("type", type);
        m_client.sendEventAsync(msg, this, this);
    }

    private synchronized Collection<IotBinder> getBindings() {
        return new LinkedList<IotBinder>(m_bindings.values());
    }

    // from com.microsoft.azure.sdk.iot.device.MessageCallback
    @Override
    public IotHubMessageResult execute(Message message, Object callbackContext) {
        JSONObject json = new JSONObject();
        for(MessageProperty property: message.getProperties()) {
            try {
                json.put(property.getName(),property.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Assumes UTF-8 encoded payload - azure specific
        try {
            json.put("payload", new String(message.getBytes()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collection<IotBinder> bindings = getBindings();
        for(IotBinder binding: bindings) {
            try {
                binding.receiveMessage(json.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return IotHubMessageResult.COMPLETE;
    }

    // from com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback
    @Override
    public DeviceMethodData call(String methodName, Object methodData, Object context) {
        Collection<IotBinder> bindings = getBindings();
        for(IotBinder binding: bindings) {
            try {
                binding.callMethod(methodName,(byte[])methodData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return new DeviceMethodData(200,"OK");
    }

    // from com.microsoft.azure.sdk.iot.device.IotHubEventCallback
    @Override
    public void execute(IotHubStatusCode responseStatus, Object callbackContext) {

    }
}

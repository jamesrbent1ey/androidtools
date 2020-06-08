package app.bentleyis.pluginprovider;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

public class DefaultProvider extends Binder implements PluginProviderAidl{
    PluginServiceConnection m_connection;
    public DefaultProvider(PluginServiceConnection connection) {
        m_connection = connection;
        // TODO start service - singleton may be good for this. this would be the binder between plugin host and the plugin
        System.out.println("DefaultProvider Connected to Host");
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public PluginServiceConnection getConnection() {
        return m_connection;
    }

    @Override
    public String getCapabilities() throws RemoteException {
        return null;
    }

    @Override
    public String notify(String message) throws RemoteException {
        return null;
    }
}

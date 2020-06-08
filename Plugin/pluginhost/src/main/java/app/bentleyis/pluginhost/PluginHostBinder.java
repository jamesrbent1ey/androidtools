package app.bentleyis.pluginhost;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

import app.bentleyis.pluginprovider.PluginHostAidl;
import app.bentleyis.pluginprovider.PluginProviderAidl;

public class PluginHostBinder extends Binder implements PluginHostAidl {
    WeakReference<PluginHostService> m_service;

    PluginHostBinder(PluginHostService service) {
        m_service = new WeakReference<>(service);
    }

    @Override
    public void registerPlugin(PluginProviderAidl plugin) throws RemoteException {

    }

    @Override
    public String getCapabilities() throws RemoteException {
        return null;
    }

    @Override
    public String notify(String message) throws RemoteException {
        return null;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }
}

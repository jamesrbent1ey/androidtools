package app.bentleyis.pluginprovider;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

/**
 * The PluginServiceConnection registers the plugin (PluginProviderAidl) with the PluginHostAidl when
 * a binding completes.
 */
public class PluginServiceConnection implements ServiceConnection {
    WeakReference<PluginProviderAidl> m_plugin;
    PluginHostAidl m_host;

    PluginServiceConnection() {

    }
    void setPlugin(PluginProviderAidl plugin) {
        m_plugin  = new WeakReference<>(plugin);
    }

    public PluginServiceConnection(PluginProviderAidl plugin) {
        setPlugin(plugin);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if(m_plugin == null || m_plugin.get() == null) {
            System.err.println("No plugin to register");
            return;
        }

        m_host = PluginHostAidl.Stub.asInterface(iBinder);
        // register
        try {
            m_host.registerPlugin(m_plugin.get());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        m_host = null;
    }

    public boolean isBound() {
        return (m_host != null);
    }

    public PluginHostAidl getHost() {
        return m_host;
    }
}

package app.bentleyis.pluginprovider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The HostResponder is a BroadcastReceiver that can be registered via manifest or Context
 * Upon receiving a broadcast, the responder should bind to the Host and register
 *
 * If a plugin is not specified in the manifest, then an implementation of PluginProviderAidl must
 * create a PluginServiceconnection and must call setConnection on an instance of this class that will
 * be registered to receive broadcasts
 *
 * When specifying this receiver in the manifest, add metadata defining the class that represents the
 * plugin. The metadata key/name should be 'plugin'. the metadata value must be the full class name
 * of the PluginProviderAidl implementation
 */
public class HostResponder extends BroadcastReceiver {
    public static final String METADATA_KEY_PLUGIN = "plugin";
    WeakReference<PluginServiceConnection> m_serviceConnection;



    public void setConnection(PluginServiceConnection connection) {
        m_serviceConnection = new WeakReference<>(connection);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // optionally get information from the manifest entry to instantiate PluginServiceConnection
        // if a connection is not set
        resolvePlugin(context);

        // validate host
        // TODO these fields should be private key encrypted for validation
        String packageName = intent.getStringExtra(Constants.INTENT_EXTRA_STRING_PACKAGE);
        String className = intent.getStringExtra(Constants.INTENT_EXTRA_STRING_CLASS);

        // bind to host and register
        if(m_serviceConnection == null || m_serviceConnection.get() == null) {
            return;
        }
        // no need to do anything if already bound.
        if(m_serviceConnection.get().isBound()) {
            return;
        }

        Intent i = new Intent();
        i.setComponent(new ComponentName(packageName,className));
        context.bindService(i,m_serviceConnection.get(),Context.BIND_AUTO_CREATE);
    }

    /**
     * Optional:  Specify the full class name of the PluginProviderAidl implementation. The implementation
     * must have a constructor that takes a single parameter of type PluginServiceConnection.class.
     */
    private void resolvePlugin(Context context) {
        if(m_serviceConnection != null && m_serviceConnection.get() != null) {
            return; // already resolved
        }
        try {
            ActivityInfo info = context.getPackageManager().getReceiverInfo(
                    new ComponentName(context,this.getClass()),
                    PackageManager.GET_META_DATA
            );
            String className = info.metaData.getString(METADATA_KEY_PLUGIN);
            createServiceConnection(className);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createServiceConnection(String className) {
        if(className == null) {
            return;
        }
        try {
            Class c = Class.forName(className);
            Constructor constructor = c.getConstructor(PluginServiceConnection.class);
            PluginServiceConnection connection = new PluginServiceConnection();
            PluginProviderAidl plugin = (PluginProviderAidl) constructor.newInstance(connection);
            connection.setPlugin(plugin);
            this.setConnection(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}

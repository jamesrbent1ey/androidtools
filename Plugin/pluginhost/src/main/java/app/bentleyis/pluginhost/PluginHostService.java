package app.bentleyis.pluginhost;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.IBinder;

import java.util.LinkedList;

import app.bentleyis.pluginprovider.Constants;
import app.bentleyis.pluginprovider.PluginProviderAidl;

/**
 * The PluginHostService periodically broadcasts an Intent in order to have Plug-ins bind and register with
 * this host.
 *
 * TODO periodicity, intent extra key names, encryption keys and action strings should all be configurable
 * TODO through meta-data entries or asset files
 */
public class PluginHostService extends Service {
    public static final String METADATA_ACTION_NAME = "action";
    LinkedList<PluginProviderAidl> m_plugins = new LinkedList<>();
    private String m_broadcastAction = "";

    public PluginHostService() {
        extractAction();
    }

    /**
     * This allows for the action to be specified as a meta-data item in the service declaration of
     * the manifest file.
     */
    private void extractAction() {
        try {
            ServiceInfo info = getPackageManager().getServiceInfo(
                    new ComponentName(this, this.getClass()),
                    PackageManager.GET_META_DATA
            );
            m_broadcastAction = info.metaData.getString(METADATA_ACTION_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        periodicallyBroadcast();
        return START_STICKY;
    }

    private void periodicallyBroadcast() {
        Intent intent = new Intent(m_broadcastAction);
        // TODO these should be private key encrypted
        intent.putExtra(Constants.INTENT_EXTRA_STRING_CLASS, getClass().getName());
        intent.putExtra(Constants.INTENT_EXTRA_STRING_PACKAGE,getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO if first time, periodically broadcast

        // validate the requester
        boolean isApplication = false;
        if(isApplication) {
            return new AppHostBinder(this);
        }

        // plugin host binder if and only if the binding is to a plugin
        return new PluginHostBinder(this);
    }

    /**
     * Register a plugin. Track the state to allow for plugins to be added/removed and suspended.
     * @param plugin
     */
    synchronized void registerPlugin(PluginProviderAidl plugin) {
        // to be able to make this call, the plugin must've been validated in onBind
        if(m_plugins.contains(plugin)) {
            return;
        }

        m_plugins.add(plugin);
    }
}

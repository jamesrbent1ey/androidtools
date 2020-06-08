package app.bentleyis.pluginhost;

import android.os.Binder;

import java.lang.ref.WeakReference;

public class AppHostBinder extends Binder {
    WeakReference<PluginHostService> m_service;

    public AppHostBinder(PluginHostService service) {
        m_service = new WeakReference<>(service);
    }
}

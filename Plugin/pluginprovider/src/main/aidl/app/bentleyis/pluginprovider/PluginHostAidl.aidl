// PluginHostAidl.aidl
package app.bentleyis.pluginprovider;

// Declare any non-default types here with import statements
import app.bentleyis.pluginprovider.PluginProviderAidl;

interface PluginHostAidl {
    void registerPlugin(PluginProviderAidl plugin);
    String getCapabilities();
    String notify(String message);
}

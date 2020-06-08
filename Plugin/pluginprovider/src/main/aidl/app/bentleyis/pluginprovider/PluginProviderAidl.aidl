// PluginProviderAidl.aidl
package app.bentleyis.pluginprovider;

// Declare any non-default types here with import statements

interface PluginProviderAidl {
    String getCapabilities();
    String notify(String message);
}

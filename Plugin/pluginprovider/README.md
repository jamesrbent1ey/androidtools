# Plugin Provider
A Plugin Provider implements a Plugin. It starts with a BroadcastReceiver, (HostResponder). When the
receiver receives an intent, it responds by attempting to bind to the indicated host. When the host
binds, the PluginSericeConnection registers with the host to expose the plugin functionality.

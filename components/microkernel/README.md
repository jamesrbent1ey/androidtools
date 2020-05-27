# Microkernel support
A Microkernel supports the Microkernel architecture pattern. This is similar to processes for OSGi and
RPC.  The Microkernel allows for modules/components to be pluggable. Other components resolve modules
through the Registry. Components are presented, in the Registry, by a common Interface class.

The Microkernel pattern provides flexibility, isolation and extensibility and is very useful for projects
that have high volatility (frequently changing requirements). This pattern is also very useful for
run-time configuration of components - such as extending existing features w/o complete revision of
the application.

The Microkernel itself has a minimalistic implementation. It currently does not resolve dependencies.
It is assumed that all component dependencies are present at run time.
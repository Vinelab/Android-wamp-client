Android-wamp-client
===================

Android-wamp-client is an open source AndroidStudio project that implements the [WAMP](http://wamp.ws/) protocol (Web Application Messaging Protocol) version 2.

The guys from [Autobahn](http://autobahn.ws/) are doing a great job implementing the WAMP protocol v1. They provided a well documented implementation for [Android](https://github.com/tavendo/AutobahnAndroid). Autobahn implementation is for WAMP v1. As listed, the v2 support is "under development" for Android.

To tickle the v2 protocol, we used [Jawampa](https://github.com/Matthias247/jawampa), a Java library providing a WAMP v2 client. The library was wrapped in AndroidStudio as a module in the example project.

## Branches

The master branch will contain an advanced WAMP provider, under development.

Checkout jawampa-basic-test branch for a basic implementation of the WAMP v2 client.

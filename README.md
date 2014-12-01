# Android-wamp-client

Android client for WAMP v2 (Web Application Messaging Protocol) based on Jawampa library

So as described before, this is "an open source AndroidStudio project that implements the WAMP protocol", targetting WAMP v2. 

Here we've made a simple project that shows how to integrate the WAMP library into your Android project.

## WAMP Service

Our case is pretty simple. The WAMP protocol is integrated to provide real-time comments service on published articles (called posts). The full service includes authentication, comments, likes and reports. Here we will only demonstrate how to connect to the WAMP router, fetch the article's comments, post a comment and listen to real-time added comments.

### Establishing Connection

To establish a connection between the Android client and the configured WAMP router, 2 main parameters needs to be set in the WampClientBuider: The host Uri and realm. In our example, a local host is configured.

    WampClientBuilder builder = new WampClientBuilder();    

    builder.withUri(WAMP_HOST)
       .withRealm(WAMP_REALM)
       .withInfiniteReconnects()
       .withReconnectInterval(MIN_RECONNECT_INTERVAL, TimeUnit.SECONDS);

    client = builder.build();

When the builder is created, call `client.open()` to open the connection.

### Connection Status Changes

The `statusChanged()` method will return an Observable object that allows to monitor the connection status of the current session between the client and the router (connected, disconnected or connecting). Subscribing to the Observable object will provide the status changes. 

    client.statusChanged().subscribe(new Action1<WampClient.Status>() {
                @Override
                public void call(WampClient.Status t1) {

                }
            });

### Requesting Comments

To request the comments list of an article or post, request a 'call' to the destined 'procedure' (comments.get). The post identifier is sent along the arguments of the call request.

    Observable<Reply> observable = client.call(procedure, arrayNode, node);
            observable.subscribe(new Action1<Reply>(){
                @Override
                public void call(Reply reply) {
                    
                }
            }, new Action1<Throwable>(){
                @Override
                public void call(Throwable arg0) {
                   
                }
            });

The desired response will be received in the arguments of the Reply object returned on call. If anything went wrong, a Throwable is returned.

### Subscribing Comments Updates (Actions)

Many actions can be predefined for comments scenario. The simplest action is when a new comment is added and broadcasted to all registered clients for the specified article (post).

In order to do that, a `makeSubscribition` call is requested on the specific procedure, in our example: "comments.[POST_ID].add".

    client.makeSubscription(procedure).subscribe(new Action1<PubSubData>(){
            @Override
            public void call(PubSubData arg0) {

            }

        }, new Action1<Throwable>(){
            @Override
            public void call(Throwable arg0) {

            }
        });

### Adding Comment

To add a new comment on the current article (post), just make a 'call' request on the specific procedure "comments.add". The request arguments will hold the needed info about the comment to be added (text, post_id, etc.). The Reply object will contain the call status returned from the server.

    Observable<Reply> observable = client.call(procedure, arrayNode, node);
    observable.subscribe(new Action1<Reply>(){
                    @Override
                    public void call(Reply reply) {

                    }
                }, new Action1<Throwable>(){
                    @Override
                    public void call(Throwable arg0) {

                    }
                });

## Wrapping Up

This example is a show case on the simplicity of the WAMP protocol implementation in Android. It highlights the asynchronous way of handling actions responses. In the traditional WebSocket implementations, all server replies are received in one place. While using WAMP, a callback is created on each request, giving you that asynchronous behavior, and eventually a more structured implementation.

The community is making a great effort to provide a full WAMP v2 solution on both back-side and client-side for different platforms. Keep tuned for updates!

 

package android.apps.vinelab.com.javawamp2client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;

public class TestActivity extends Activity
{
    WampClient client;
    String LOG_WAMP = "WAMP";
    // config
    String WAMP_HOST = "ws://192.168.17.195:9090";
    String WAMP_REALM = "vinelab";
    int MIN_RECONNECT_INTERVAL = 5; // sec
    int POST_ID = 52213;
    int USER_ID = 52188;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        connectWampClient();
    }

    void connectWampClient()
    {
        try {
            // Create a builder and configure the client
            WampClientBuilder builder = new WampClientBuilder();
            builder.withUri(WAMP_HOST)
                    .withRealm(WAMP_REALM)
                    .withInfiniteReconnects()
                    .withReconnectInterval(MIN_RECONNECT_INTERVAL, TimeUnit.SECONDS);
            // Create a client through the builder. This will not immediately start
            // a connection attempt
            client = builder.build();

		   /* // Create a Status Observer to track the status of the WAMP connection
		    Observable<Status> status = client.statusChanged();
		    status.subscribe(new Observer<Status>() {
				@Override
				public void onCompleted() {
					Log.i(LOG_WAMP, "onCompled");
				}

				@Override
				public void onError(Throwable arg0) {
					Log.i(LOG_WAMP, "onError");
				}

				@Override
				public void onNext(Status arg0) {
					Log.i(LOG_WAMP, "onNext");
					if(arg0 == Status.Connected) {
						Log.i(LOG_WAMP, "Connected");
						requestComments();
					}
				}
			});*/

            client.statusChanged().subscribe(new Action1<WampClient.Status>() {
                @Override
                public void call(WampClient.Status t1) {
                    Log.i(LOG_WAMP,"Session status changed to " + t1);

                    if(t1 == WampClient.Status.Connected) {
                        requestComments();
                    }
                }
            });
            // request to open the connection with the server
            client.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void requestComments()
    {
        try {
            JSONObject json = new JSONObject();
            json.put("post_id", 52213);

            JsonNodeFactory jnf = new JsonNodeFactory(true);
            ObjectNode node = new ObjectNode(jnf);
            node.put("post_id", 52213);
            ArrayNode arrayNode = new ArrayNode(jnf);


            Observable<Reply> observable = client.call("comments.get", arrayNode, node);
            observable.subscribe(new Action1<Reply>(){
                @Override
                public void call(Reply reply) {
                    setCommentsAddListener();
                    addComment("comment 1");
                    if(reply != null) {
                        ArrayNode arguments = reply.arguments();
                        String str = arguments.toString();
                        try {
                            JSONArray jsonArray = new JSONArray(str);
                            int count = jsonArray.length();
                            if(count > 0) {

                            }
                            Log.i(LOG_WAMP, "comments.get call Json response: " + str);
                        }
                        catch (JSONException e) {}
                    }
                }
            }, new Action1<Throwable>(){
                @Override
                public void call(Throwable arg0) {
                    if(arg0 != null) {
                        Log.i(LOG_WAMP, "comments.get call Throwable response: " + arg0.toString());
                    }
                }

            });

			/*client.call("comments.get", JSONObject.class, 52213).subscribe(new Action1<JSONObject>(){
				@Override
				public void call(JSONObject arg0) {
					if(arg0 != null) {
						Log.i(LOG_WAMP, "comments.get call Json response: " + arg0.toString());
					}
				}
			}, new Action1<Throwable>(){
				@Override
				public void call(Throwable arg0) {
					if(arg0 != null) {
						Log.i(LOG_WAMP, "comments.get call Throwable response: " + arg0.toString());
					}
				}

			});*/
        }
        catch (Exception e) {
            Log.i(LOG_WAMP, "requestComments Exception");
        }
    }

    void setCommentsAddListener()
    {
        // comments.[post_id].add
        final String procedure = "comments." + POST_ID + ".add";

        client.makeSubscription(procedure).subscribe(new Action1<PubSubData>(){
            @Override
            public void call(PubSubData arg0) {
                if(arg0 != null) {
                    Log.i(LOG_WAMP, procedure + " call Json response: " + arg0.toString());
                    ObjectNode objectNode = arg0.keywordArguments();
                    String str = objectNode.toString();
                    try {
                        JSONObject jsonComment = new JSONObject(str);
                    }
                    catch (JSONException e) {}
                }
            }

        }, new Action1<Throwable>(){
            @Override
            public void call(Throwable arg0) {
                if(arg0 != null) {
                    Log.i(LOG_WAMP, procedure + " call Throwable response: " + arg0.toString());
                }
            }

        });

		/*client.makeSubscription(procedure, String.class).subscribe(new Action1<String>() {
			@Override
			public void call(String arg0) {
				if(arg0 != null) {
					Log.i(LOG_WAMP, procedure + " call Json response: " + arg0.toString());
				}
			}

		}, new Action1<Throwable>(){
			@Override
			public void call(Throwable arg0) {
				if(arg0 != null) {
					Log.i(LOG_WAMP, procedure + " call Throwable response: " + arg0.toString());
				}
			}

		});*/

		/*client.call(procedure, JSONObject.class, (Object) null).subscribe(new Action1<JSONObject>(){
			@Override
			public void call(JSONObject arg0) {
				if(arg0 != null) {
					Log.i(LOG_WAMP, procedure + " call Json response: " + arg0.toString());
				}
			}
		}, new Action1<Throwable>(){
			@Override
			public void call(Throwable arg0) {
				if(arg0 != null) {
					Log.i(LOG_WAMP, procedure + " call Throwable response: " + arg0.toString());
				}
			}

		});*/
    }

    void addComment(String comment)
    {
        final String procedure = "comments.add";

        try {
            if(comment != null && comment.trim().length() > 0) {
                JsonNodeFactory jnf = new JsonNodeFactory(true);
                ObjectNode node = new ObjectNode(jnf);
                node.put("post_id", POST_ID);
                node.put("user_id", String.valueOf(USER_ID));
                node.put("comment", comment);
                ArrayNode arrayNode = new ArrayNode(jnf);

                Observable<Reply> observable = client.call(procedure, arrayNode, node);
                observable.subscribe(new Action1<Reply>(){
                    @Override
                    public void call(Reply reply) {
                        if(reply != null) {
                            ArrayNode arguments = reply.arguments();
                            String str = arguments.toString();
                            Log.i(LOG_WAMP, procedure + " call Json response: " + str);
                            try {
                                JSONArray jsonArray = new JSONArray(str);
                                JSONObject jsonResp = jsonArray.getJSONObject(0);
                                int status = jsonResp.getInt("status");
                                if(status == 200) {
                                    Log.i(LOG_WAMP, "Comment added successfully");
                                }
                                else {
                                    Log.i(LOG_WAMP, "Operation failed");
                                }
                            }
                            catch (Exception e) {}
                        }
                    }
                }, new Action1<Throwable>(){
                    @Override
                    public void call(Throwable arg0) {
                        if(arg0 != null) {
                            Log.i(LOG_WAMP, procedure + " call Throwable response: " + arg0.toString());
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            Log.i(LOG_WAMP, procedure + " Exception");
        }
    }
}

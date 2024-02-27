package cloud.inferno_team.custompusher;

import com.pusher.client.*;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.channel.Channel;
import com.pusher.client.util.HttpChannelAuthorizer;

public class PusherUtil {
    private static Pusher pusher;

    private PusherUtil() {}

    public static void init(String hostName,int port, String clusterName, boolean useTLS) { // clusterName :mt1
        PusherOptions options = new PusherOptions().setCluster(clusterName);
        options.setWsPort(port); // 6001
        options.setHost(hostName);
        options.setUseTLS(useTLS);
        options.setChannelAuthorizer(new ChannelAuthorizer() {
            @Override
            public String authorize(String channelName, String socketId) throws AuthorizationFailureException {
                return null;
            }
        });
        pusher = new Pusher("myKey", options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
//                JOptionPane.showMessageDialog(null,
//                        "can't connect to web socket.",
//                        "There was a problem connecting!.",
//                        JOptionPane.ERROR_MESSAGE);
//                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
    }

    /*   public static void listen(String channelName, String eventName, SubscriptionEventListener listener) {

           Channel channel = pusher.subscribe(channelName);

           channel.bind(eventName, listener);
           pusher.disconnect();

           pusher.connect();
       }*/
    public static void listen(String channelName, String eventName, PresenceChannelEventListener listener) {

        Channel channel = pusher.subscribePresence(channelName);

        channel.bind(eventName, listener);

        pusher.disconnect();

        pusher.connect();
    }

    public static String authorize(String channelName, int kioskId) {
        String id = pusher.getConnection().getSocketId();

        HttpChannelAuthorizer authorizer = new HttpChannelAuthorizer("YOUR_AUTH_ENDPOINT");
        return authorizer.authorize(channelName, id);
//        authorizer.setHeaders("Content-Type", "application/json");

    }

    public static void check() {
        ConnectionState state = pusher.getConnection().getState();
        System.out.println(state);
//        System.out.println(pusher.getPresenceChannel("presence-kiosks").isSubscribed());
//        System.out.println(pusher.getPresenceChannel("presence-kiosks").getMe());
        if (pusher.getPresenceChannel("presence-kiosks").isSubscribed()) {
            pusher.getPresenceChannel("presence-kiosks").
                    trigger("presence-kiosks.print", "{\"url\":1}");
        }
    }
}

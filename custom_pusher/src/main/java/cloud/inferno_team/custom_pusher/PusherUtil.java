package cloud.inferno_team.custom_pusher;

import androidx.annotation.Nullable;

import com.pusher.client.*;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.channel.Channel;
import com.pusher.client.util.HttpChannelAuthorizer;

class PusherUtil {
    static Pusher pusher;

    private PusherUtil() {
    }

    static void init(String key, String hostName, int port, String clusterName, boolean useTLS) {

        PusherOptions options = new PusherOptions().setCluster(clusterName);// clusterName :mt1
        options.setWsPort(port); // 6001
        options.setHost(hostName);
        options.setUseTLS(useTLS);
        pusher = new Pusher(key, options);
    }


    /*   public static void listen(String channelName, String eventName, SubscriptionEventListener listener) {

           Channel channel = pusher.subscribe(channelName);

           channel.bind(eventName, listener);
           pusher.disconnect();

           pusher.connect();
       }*/
    static void listenPresence(String channelName, String eventName, PresenceChannelEventListener listener) {

        Channel channel = pusher.subscribePresence(channelName);

        channel.bind(eventName, listener);

        pusher.disconnect();

        pusher.connect();
    }

    static String authorize(String channelName, int kioskId) {
        String id = pusher.getConnection().getSocketId();

        HttpChannelAuthorizer authorizer = new HttpChannelAuthorizer("YOUR_AUTH_ENDPOINT");
        return authorizer.authorize(channelName, id);
//        authorizer.setHeaders("Content-Type", "application/json");

    }

    static void check() {
        ConnectionState state = pusher.getConnection().getState();
        System.out.println(state);
//        System.out.println(pusher.getPresenceChannel("presence-kiosks").isSubscribed());
//        System.out.println(pusher.getPresenceChannel("presence-kiosks").getMe());
        if (pusher.getPresenceChannel("presence-kiosks").isSubscribed()) {
            pusher.getPresenceChannel("presence-kiosks").trigger("presence-kiosks.print", "{\"url\":1}");
        }
    }
}

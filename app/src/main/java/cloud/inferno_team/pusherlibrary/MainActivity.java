package cloud.inferno_team.pusherlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import cloud.inferno_team.custom_pusher.CustomPusher;
import cloud.inferno_team.custom_pusher.services.PusherService;
import cloud.inferno_team.custom_pusher.services.Restarter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, PusherService.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);

        }
//        try {
//            CustomPusher customPusher = CustomPusher.init(this);
//            customPusher.connect(new ConnectionEventListener() {
//                @Override
//                public void onConnectionStateChange(ConnectionStateChange change) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this,
//                                "State changed to " + change.getCurrentState() +
//                                        " from " + change.getPreviousState()
//                                , Toast.LENGTH_SHORT).show();
//                    });
//                    System.out.println("State changed to " + change.getCurrentState() +
//                            " from " + change.getPreviousState());
//                }
//
//                @Override
//                public void onError(String message, String code, Exception e) {
//                    System.out.println("on error" + message);
//                }
//            }, ConnectionState.ALL);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }

}
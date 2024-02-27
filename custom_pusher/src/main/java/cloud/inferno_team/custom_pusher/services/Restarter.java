package cloud.inferno_team.custom_pusher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Restarter", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, PusherService.class));
    }
}

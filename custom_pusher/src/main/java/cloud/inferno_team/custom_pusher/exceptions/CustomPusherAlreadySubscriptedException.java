package cloud.inferno_team.custom_pusher.exceptions;

import org.jetbrains.annotations.NotNull;

public class CustomPusherAlreadySubscriptedException extends Exception{
    public CustomPusherAlreadySubscriptedException(@NotNull String message) {
        super(message);
    }
}

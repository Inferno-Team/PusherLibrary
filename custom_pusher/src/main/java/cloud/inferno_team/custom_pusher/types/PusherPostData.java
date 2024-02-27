package cloud.inferno_team.custom_pusher.types;

public class PusherPostData<T> {
    private final T data;
    private final PusherEventType type;

    public PusherPostData(T data, PusherEventType type) {
        this.data = data;
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public PusherEventType getType() {
        return type;
    }
}

package cloud.inferno_team.custom_pusher.types;

public class NotificationObject {
    private final String title;
    private final String description;
    private final String imageUrl;

    public NotificationObject(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

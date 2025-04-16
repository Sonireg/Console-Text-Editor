package editor.User.Subscriprions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationManager {
    private final SubscriptionManager subscriptionManager;
    private final NotificationStorage storage;

    public NotificationManager(SubscriptionManager subscriptionManager, NotificationStorage storage) {
        this.subscriptionManager = subscriptionManager;
        this.storage = storage;
    }

    public void notifySubscribers(String fileName, int snapshotCount) {
        var subscribers = subscriptionManager.getSubscribers(fileName);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = String.format("%s changed at %s. New version: history %s %d",
                fileName, time, fileName, snapshotCount);

        for (String user : subscribers) {
            try {
                storage.addNotification(user, message);
            } catch (IOException e) {
                System.out.println("Error saving notification for " + user + ": " + e.getMessage());
            }
        }
    }
}

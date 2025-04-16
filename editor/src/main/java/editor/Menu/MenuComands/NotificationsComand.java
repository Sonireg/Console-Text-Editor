package editor.Menu.MenuComands;

import java.io.IOException;
import java.util.List;

import editor.User.UserManager;
import editor.User.Subscriprions.NotificationStorage;

public class NotificationsComand {
    public static void handle(String[] args, UserManager userManager) {
        String username = userManager.getCurrentUser().getUsername();
        NotificationStorage storage = new NotificationStorage();
        if (args.length > 1) {
            if (args[1].equals("-c")) {
                try {
                    storage.clearNotifications(username);
                } catch (IOException e) {
                    System.out.println("Error while reading notifications: " + e.getMessage());
                }
                return;
            }
            System.out.println("Usage: 'notifications' or 'notifications -c'");
            return;
        }

        try {
            List<String> notes = storage.loadNotifications(username);
            if (notes.isEmpty()) {
                System.out.println("No notifications");
            } else {
                System.out.println("Notifications:");
                notes.forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Error while reading notifications: " + e.getMessage());
        }
    }
}

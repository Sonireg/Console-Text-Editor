package editor.Menu.MenuComands;

import editor.User.UserManager;

public class WhoAmIComand {
    public static void handle(UserManager userManager) {
        if (userManager.isLoggedIn()) {
            System.out.println("Current user: " + userManager.getCurrentUser().getUsername());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
}

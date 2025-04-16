package editor.Menu.MenuComands;

import editor.User.UserManager;

public class LoginComand {
    public static void handle(String[] args, UserManager userManager) {
        if (args.length < 2) {
            System.out.println("Usage: login <username>");
            return;
        }
        var user = userManager.login(args[1]);
        System.out.println("Logged in as: " + user.getUsername());
    }
}

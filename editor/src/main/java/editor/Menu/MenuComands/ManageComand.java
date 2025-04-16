package editor.Menu.MenuComands;

import editor.User.UserManager;

public class ManageComand {
    public static void handle(String[] args, UserManager userManager) {

        if (args.length < 4) {
            System.out.println("Usage: manage <filename> <username> <role>");
            return;
        }
    
        if (!userManager.isLoggedIn()) {
            System.out.println("You must login first.");
            return;
        }
    
        String fileName = args[1];
        String targetUsername = args[2];
        String role = args[3].toLowerCase();
    
        boolean result = userManager.manageFile(fileName, targetUsername, role);
        if (result) {
            System.out.printf("User '%s' is now %s for file '%s'%n", targetUsername, role, fileName);
        } else {
            System.out.println("Failed to update permissions. Check your access rights or role.");
        }
    }
}

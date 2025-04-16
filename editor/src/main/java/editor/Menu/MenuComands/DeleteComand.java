package editor.Menu.MenuComands;

import editor.User.UserManager;

public class DeleteComand {
    public static void handle(String[] args, UserManager userManager) {
        if (args.length != 2) {
            System.out.println("Usage: delete <file_name>");
            return;
        }
        String fileName = args[1];
        userManager.getCurrentUser().deleteFile(fileName);
    }
}

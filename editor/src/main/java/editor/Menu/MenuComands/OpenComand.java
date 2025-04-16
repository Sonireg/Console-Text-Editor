package editor.Menu.MenuComands;

import java.io.IOException;

import editor.User.UserManager;
import editor.User.Roles.EditorRole;
import editor.User.Roles.ViewerRole;

public class OpenComand {
    public static void handle(String[] args, UserManager userManager) throws IOException{
        if (args.length < 3) {
            System.out.println("Usage: open <filename> <role>");
            return;
        }

        String fileName = args[1];
        String role = args[2];
        switch (role) {
            case "-e" -> userManager.getCurrentUser().setOpeningRole(new EditorRole());
            case "-v" -> userManager.getCurrentUser().setOpeningRole(new ViewerRole());
        }
        System.out.println(userManager.getCurrentUser().openFile(fileName));
    }
}

package editor;

import java.io.IOException;
import java.util.Scanner;

import editor.User.UserManager;
import editor.User.Permissions.PermissionsManager;
import editor.User.Roles.EditorRole;
import editor.User.Roles.ViewerRole;

public class Menu {

    private static UserManager userManager;
    
    public static void menuCycle() throws IOException {
        PermissionsManager permissionsManager = new PermissionsManager();
        userManager = new UserManager(permissionsManager);

        Scanner scanner = new Scanner(System.in);

        boolean working = true;
        while (working) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) continue;

            String[] args = command.split(" ");
            String keyword = args[0].toLowerCase();

            switch (keyword) {
                case "login"-> handleLogin(args);
                case "whoami"-> handleWhoAmI();
                case "open" -> handleOpen(args);
                case "manage" -> handleManage(args);
                case "ls" -> handleList(args);
                case "exit" -> {
                    handleExit();
                    working = false;
                    break;
                }
                default -> System.out.println("Unknown command.");
            }
        }

        scanner.close();
    }




    private static void handleLogin(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: login <username>");
            return;
        }
        var user = userManager.login(args[1]);
        System.out.println("Logged in as: " + user.getUsername());
    }




    private static void handleWhoAmI() {
        if (userManager.isLoggedIn()) {
            System.out.println("Current user: " + userManager.getCurrentUser().getUsername());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }



    private static void handleOpen(String[] args) throws IOException{
        if (args.length < 3) {
            System.out.println("Usage: open <filename> <role>");
            return;
        }

        if (!userManager.isLoggedIn()) {
            System.out.println("You must login first.");
            return;
        }



        String fileName = args[1];
        String role = args[2];
        switch (role) {
            case "-e" -> userManager.getCurrentUser().setOpeningRole(new EditorRole());
            case "-v" -> userManager.getCurrentUser().setOpeningRole(new ViewerRole());
        }
        userManager.getCurrentUser().openFile(fileName);
    }




    private static void handleExit() {
        System.out.print("\033[2J\033[H");
        System.out.println("Exiting...");
    }

    private static void handleManage(String[] args) {
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

    
    private static void handleList(String[] args) {
        if (!userManager.isLoggedIn()) {
            System.out.println("You must login first.");
            return;
        }
    
        if (args.length == 1) {
            // ls — все файлы
            userManager.listAllFiles();
        } else {
            switch (args[1]) {
                case "-e" -> userManager.listEditorFiles();
                case "-a" -> userManager.listAdminFiles();
                default -> System.out.println("Unknown option for ls. Use ls, ls -e, or ls -a.");
            }
        }
    }
}

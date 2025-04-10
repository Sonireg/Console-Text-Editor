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
}

package editor.Menu.MenuComands;

import java.util.List;
import java.util.Set;

import editor.User.UserManager;
import editor.User.Permissions.FilePermissions;
import editor.User.Subscriprions.SubscriptionManager;

public class ListComand {

    public static void handle(String[] args,
                              UserManager userManager,
                              SubscriptionManager subscriptionManager) {

        if (args.length == 1) {
            userManager.listAllFiles();
            return;
        }

        String option = args[1];

        switch (option) {
            case "-e" -> userManager.listEditorFiles();
            case "-a" -> userManager.listAdminFiles();
            case "-u" -> listAllUsers(userManager);
            case "-s" -> listSubscriptions(userManager, subscriptionManager);
            case "-f" -> handleFileSpecificListing(args, userManager);
            case "-af" -> listAllFilenames(userManager);
            default -> printUsage();
        }
    }

    private static void listAllUsers(UserManager userManager) {
        Set<String> allUsers = userManager.getPermissionsManager().getUsers();
        System.out.println("All users:");
        allUsers.forEach(System.out::println);
    }

    private static void listSubscriptions(UserManager userManager, SubscriptionManager subscriptionManager) {
        String username = userManager.getCurrentUser().getUsername();
        List<String> subscriptions = subscriptionManager.getSubscriptions(username);

        System.out.println("Subscriptions:");
        if (subscriptions.isEmpty()) {
            System.out.println("No subscriptions");
        } else {
            subscriptions.forEach(System.out::println);
        }
    }

    private static void handleFileSpecificListing(String[] args, UserManager userManager) {
        if (args.length != 4) {
            System.out.println("Usage: ls -f <filename> -a | -e");
            return;
        }

        String fileName = args[2];
        String subOption = args[3];

        switch (subOption) {
            case "-a" -> listFileAdmin(fileName, userManager);
            case "-e" -> listFileEditors(fileName, userManager);
            default -> System.out.println("Unknown option for ls -f. Use -a (admin) or -e (editors).");
        }
    }

    private static void listFileAdmin(String fileName, UserManager userManager) {
        FilePermissions perms = getFilePermissions(fileName, userManager);
        if (perms != null) {
            System.out.println(fileName + " admin is " + perms.getAdmin());
        }
    }

    private static void listFileEditors(String fileName, UserManager userManager) {
        FilePermissions perms = getFilePermissions(fileName, userManager);
        if (perms != null) {
            Set<String> editors = perms.getEditors();
            if (editors.isEmpty()) {
                System.out.println("File " + fileName + " has no editors.");
            } else {
                System.out.println(fileName + " editors are: ");
                editors.forEach(System.out::println);
            }
        }
    }

    private static FilePermissions getFilePermissions(String fileName, UserManager userManager) {
        FilePermissions perms = userManager.getPermissionsManager().getPermissions(fileName);
        if (perms == null) {
            System.out.println("File not found in permissions.json.");
        }
        return perms;
    }

    private static void listAllFilenames(UserManager userManager) {
        System.out.println("Все файлы:");
        userManager.getPermissionsManager()
                   .getAllFilenames()
                   .forEach(System.out::println);
    }

    private static void printUsage() {
        System.out.println("Unknown option for ls.");
        System.out.println("Available options:");
        System.out.println("  ls             - list all current user's files");
        System.out.println("  ls -e          - list files with edit access");
        System.out.println("  ls -a          - list files with admin access");
        System.out.println("  ls -u          - list all users");
        System.out.println("  ls -s          - list subscriptions");
        System.out.println("  ls -f <file> -a|-e  - list file's admin or editors");
        System.out.println("  ls -af         - list all known filenames");
    }
}

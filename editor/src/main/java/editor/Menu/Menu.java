package editor.Menu;

import java.io.IOException;
import java.util.Scanner;

import editor.User.UserManager;
import editor.User.Permissions.PermissionsManager;
import editor.User.Subscriprions.SubscriptionManager;
import editor.Menu.MenuComands.LoginComand;
import editor.Menu.MenuComands.WhoAmIComand;
import editor.Menu.MenuComands.OpenComand;
import editor.Menu.MenuComands.ManageComand;
import editor.Menu.MenuComands.ListComand;
import editor.Menu.MenuComands.DeleteComand;
import editor.Menu.MenuComands.HistoryComand;
import editor.Menu.MenuComands.NotificationsComand;
import editor.Menu.MenuComands.SubscribeCommand;
import editor.Menu.MenuComands.HelpComand;

public class Menu {

    private static UserManager userManager;
    private static final SubscriptionManager subscriptionManager = new SubscriptionManager();
    
    public static void menuCycle() throws IOException {
        PermissionsManager permissionsManager = new PermissionsManager();
        userManager = new UserManager(permissionsManager);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) continue;

            String[] args = command.split(" ");
            String keyword = args[0].toLowerCase();
            if (keyword.equals("exit")) {
                handleExit();
                break;
            }
            if (keyword.equals("login")) {
                LoginComand.handle(args, userManager);
                continue;
            }

            if (!checkLogin()) continue;

            switch (keyword) {
                case "whoami"-> WhoAmIComand.handle(userManager);
                case "open" -> OpenComand.handle(args, userManager);
                case "manage" -> ManageComand.handle(args, userManager);
                case "ls" -> ListComand.handle(args, userManager, subscriptionManager);
                case "delete" -> DeleteComand.handle(args, userManager);
                case "history" -> HistoryComand.handle(args, userManager);
                case "notifications" -> NotificationsComand.handle(args, userManager);
                case "subscribe" -> SubscribeCommand.handleSubscribe(args, userManager, subscriptionManager);
                case "unsubscribe" -> SubscribeCommand.handleUnsubscribe(args, userManager, subscriptionManager);
                case "help" -> HelpComand.handle(args);
                default -> System.out.println("Unknown command.");
            }
        }

        scanner.close();
    }


    private static void handleExit() {
        System.out.print("\033[2J\033[H");
        System.out.println("Exiting...");
    }


    static boolean checkLogin() {
        if (!userManager.isLoggedIn()) {
            System.out.println("You must login first.");
            return false;
        }
        return true;
    }
}

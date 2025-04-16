package editor.Menu.MenuComands;

import editor.User.UserManager;
import editor.User.Subscriprions.SubscriptionManager;

public class SubscribeCommand {
    public static void handleSubscribe(String[] args, 
    UserManager userManager, 
    SubscriptionManager subscriptionManager) {
        if (args.length < 2) {
            System.out.println("Usage: subscribe <filename>");
            return;
        }
    
        String username = userManager.getCurrentUser().getUsername();
        String filename = args[1];
    
        subscriptionManager.subscribe(username, filename);
        System.out.println("You subscribed to file " + filename);
    }


    public static void handleUnsubscribe(String[] args, 
    UserManager userManager, 
    SubscriptionManager subscriptionManager) {
        if (args.length < 2) {
            System.out.println("Usage: unsubscribe <filename>");
            return;
        }
    
        String username = userManager.getCurrentUser().getUsername();
        String filename = args[1];
    
        subscriptionManager.unsubscribe(username, filename);
        System.out.println("You unsubscribed from file " + filename);
    }
}

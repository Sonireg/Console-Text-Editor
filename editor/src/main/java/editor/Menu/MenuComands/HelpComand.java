package editor.Menu.MenuComands;

import java.util.HashMap;
import java.util.Map;

public class HelpComand {

    private static final Map<String, String> commandDescriptions = new HashMap<>();

    static {
        commandDescriptions.put("login", "login <username> - Log in or create a new user.");
        commandDescriptions.put("whoami", "whoami - Show the currently logged in user.");
        commandDescriptions.put("open", "open <filename> <role> - Open a file with specified role (-e for editor, -v for viewer).");
        commandDescriptions.put("manage", "manage <filename> <username> <role> - Assign a role (admin/editor/viewer) to a user for a file.");
        commandDescriptions.put("ls", """
            ls [options] - List files or users. Options:
              -e — your editor files
              -a — your admin files
              -af — all files
              -u — all users
              -s — your subscriptions
              -f <filename> -a | -e — admins or editors of a file
            """);
        commandDescriptions.put("delete", "delete <filename> - Delete the specified file.");
        commandDescriptions.put("history", "history <filename> [--count | version_number] - Show version count or open a specific version.");
        commandDescriptions.put("notifications", "notifications [-c] - Show or clear your notifications.");
        commandDescriptions.put("subscribe", "subscribe <filename> - Subscribe to changes of the specified file.");
        commandDescriptions.put("unsubscribe", "unsubscribe <filename> - Unsubscribe from changes of the specified file.");
        commandDescriptions.put("help", "help [command] - Show all commands or detailed help for a specific command.");
        commandDescriptions.put("exit", "exit - Exit the application.");
        commandDescriptions.put("editor", """
            Keyboard shortcuts in editor mode:
              Arrow Keys         - Move cursor
              Shift + Arrow Keys - Select text
              Ctrl + Z           - Undo
              Ctrl + U           - Redo
              Ctrl + C           - Copy
              Ctrl + V           - Paste
              Ctrl + X           - Cut
              Ctrl + S           - Save file
              Ctrl + N           - Open file
              Ctrl + O           - Exit editor
              Ctrl + L           - Change theme
              Ctrl + T           - Change font size
              Ctrl + H           - Show help in editor
            """);
    }

    public static void handle(String[] args) {
        if (args.length == 1) {
            System.out.println("Available commands:");
            for (var entry : commandDescriptions.entrySet()) {
                System.out.println(" - " + entry.getKey());
            }
            System.out.println("\nType 'help <command>' for details about a specific command.");
        } else {
            String command = args[1];
            String description = commandDescriptions.get(command);
            if (description != null) {
                System.out.println(description);
            } else {
                System.out.println("Unknown command: " + command);
            }
        }
    }
}

package editor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import editor.BasicEditor.Saving.HistoryManager;
import editor.User.UserManager;
import editor.User.Permissions.FilePermissions;
import editor.User.Permissions.PermissionsManager;
import editor.User.Roles.EditorRole;
import editor.User.Roles.ViewerRole;
import editor.User.Subscriprions.NotificationStorage;
import editor.User.Subscriprions.SubscriptionManager;

public class Menu {

    private static UserManager userManager;
    private static final SubscriptionManager subscriptionManager = new SubscriptionManager();
    
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
                case "delete" -> handleDelete(args);
                case "history" -> handleHistory(args);
                case "notifications" -> handleNotifications(args);
                case "subscribe" -> handleSubscribe(args);
                case "unsubscribe" -> handleUnsubscribe(args);
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


    private static void handleDelete(String[] args) {
        if (!checkLogin()) return;
        if (args.length != 2) {
            System.out.println("Использование: delete file_name");
            return;
        }
        String fileName = args[1];
        userManager.getCurrentUser().deleteFile(fileName);
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
        if (!checkLogin()) return;
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




    private static void handleExit() {
        System.out.print("\033[2J\033[H");
        System.out.println("Exiting...");
    }

    private static void handleManage(String[] args) {
        if (!checkLogin()) return;

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
        if (!checkLogin()) return;

        if (args.length == 1) {
            userManager.listAllFiles();
            return;
        }

        switch (args[1]) {
            case "-e" -> userManager.listEditorFiles();
            case "-a" -> userManager.listAdminFiles();
            case "-u" -> handleListUsers();
            case "-s" -> {
                List<String> subscriptions = subscriptionManager.
                                            getSubscriptions(userManager.getCurrentUser().getUsername());
                System.out.println("Ваши подписки:");
                if (subscriptions.isEmpty()) {
                    System.out.println("Нет подписок.");
                } else {
                    subscriptions.forEach(System.out::println);
                }
            }
            case "-f" -> {
                if (args.length != 4) {
                    System.out.println("Usage: ls -f <filename> -a | -e");
                    return;
                }
                String fileName = args[2];
                switch (args[3]) {
                    case "-a" -> handleListFileAdmin(fileName);
                    case "-e" -> handleListFileEditors(fileName);
                    default -> System.out.println("Unknown option for ls -f. Use -a (admin) or -e (editors).");
                }
            }
            case "-af" -> {
                System.out.println("Все файлы:");
                userManager.getPermissionsManager().getAllFilenames().forEach(System.out::println);
            }
            default -> System.out.println("Unknown option for ls. Use ls, ls -e, ls -a, ls -f <filename> -a/-e, or ls -u.");
        }
    }

    private static void handleListUsers() {
        PermissionsManager pm = userManager.getPermissionsManager();
        Set<String> allUsers = pm.getUsers();
        System.out.println("Все пользователи:");
        allUsers.forEach(System.out::println);
    }

    private static void handleListFileAdmin(String fileName) {
        PermissionsManager pm = userManager.getPermissionsManager();
        FilePermissions perms = pm.getPermissions(fileName);
        if (perms == null) {
            System.out.println("Файл не найден в permissions.json.");
            return;
        }
        System.out.println("Администратор файла " + fileName + ": " + perms.getAdmin());
    }

    private static void handleListFileEditors(String fileName) {
        PermissionsManager pm = userManager.getPermissionsManager();
        FilePermissions perms = pm.getPermissions(fileName);
        if (perms == null) {
            System.out.println("Файл не найден в permissions.json.");
            return;
        }
        Set<String> editors = perms.getEditors();
        if (editors.isEmpty()) {
            System.out.println("У файла " + fileName + " нет редакторов.");
        } else {
            System.out.println("Редакторы файла " + fileName + ":");
            editors.forEach(System.out::println);
        }
    }


    private static void handleHistory(String[] args) {
        if (!checkLogin()) return;
        if (args.length < 2) {
            System.out.println("Неверный синтаксис команды history.");
            return;
        }

        String fileName = args[1];

        if (args.length == 3 && args[2].equals("--count")) {
            int count = HistoryManager.getSnapshotCount(fileName);
            System.out.println("Всего версий: " + count);
            return;
        }

        if (args.length == 3) {
            try {
                int version = Integer.parseInt(args[2]);
                String content = HistoryManager.getSnapshot(fileName, version);
                if (content == null) {
                    System.out.println("Такой версии не существует.");
                    return;
                }

                // Создаём временный файл
                String ext = getFileExtension(fileName);
                String tempName = fileName.replace("." + ext, "") + "TEMP." + ext;
                Files.write(Path.of(tempName), content.getBytes());

                // Смотрим его как Viewer
                userManager.getCurrentUser().setOpeningRole(new ViewerRole());
                userManager.getCurrentUser().openFile(tempName);

            } catch (NumberFormatException | IOException e) {
                System.out.println("Ошибка при открытии истории: " + e.getMessage());
            }
        } else {
            System.out.println("Неверный синтаксис команды history.");
        }
    }

    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1) : "";
    }


    private static void handleNotifications(String[] args) {
        if (!checkLogin()) return;
        String username = userManager.getCurrentUser().getUsername();
        NotificationStorage storage = new NotificationStorage();
        if (args.length > 1) {
            if (args[1].equals("-c")) {
                try {
                    storage.clearNotifications(username);
                } catch (IOException e) {
                    System.out.println("Ошибка при чтении уведомлений: " + e.getMessage());
                }
                return;
            }
            System.out.println("Использование без флагов выводит уведомления. Флаг -c для того, чтобы удалить все уведомления");
            return;
        }

        try {
            List<String> notes = storage.loadNotifications(username);
            if (notes.isEmpty()) {
                System.out.println("У вас нет новых уведомлений.");
            } else {
                System.out.println("Уведомления:");
                notes.forEach(System.out::println);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении уведомлений: " + e.getMessage());
        }
    }


    private static void handleSubscribe(String[] args) {
        if (!checkLogin()) return;
        if (args.length < 2) {
            System.out.println("Usage: subscribe <filename>");
            return;
        }
    
        String username = userManager.getCurrentUser().getUsername();
        String filename = args[1];
    
        subscriptionManager.subscribe(username, filename);
        System.out.println("Вы подписались на изменения файла " + filename);
    }


    private static void handleUnsubscribe(String[] args) {
        if (!checkLogin()) return;
        if (args.length < 2) {
            System.out.println("Usage: unsubscribe <filename>");
            return;
        }
    
        String username = userManager.getCurrentUser().getUsername();
        String filename = args[1];
    
        subscriptionManager.unsubscribe(username, filename);
        System.out.println("Вы отписались от файла " + filename);
    }


    static boolean checkLogin() {
        if (!userManager.isLoggedIn()) {
            System.out.println("You must login first.");
            return false;
        }
        return true;
    }
}

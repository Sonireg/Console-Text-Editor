package editor.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import editor.User.Permissions.PermissionsManager;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final PermissionsManager permissionsManager;
    private User currentUser;

    public UserManager(PermissionsManager permissionsManager) {
        this.permissionsManager = permissionsManager;
    }

    public User login(String username) {
        users.putIfAbsent(username, new User(username, permissionsManager));
        currentUser = users.get(username);
        permissionsManager.addUser(username); // добавим в users.json
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean manageFile(String fileName, String targetUsername, String role) {
        if (!permissionsManager.isAdmin(fileName, currentUser.getUsername())) {
            return false;
        }
    
        switch (role) {
            case "editor" -> permissionsManager.addEditor(fileName, targetUsername);
            case "viewer" -> permissionsManager.removeEditor(fileName, targetUsername);
            case "admin" -> permissionsManager.setAdmin(fileName, targetUsername);
            default -> {
                return false;
            }
        }
        return true;
    }


    public void listAllFiles() {
        Set<String> files = permissionsManager.getAllFilenames();
        if (files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("All files:");
            files.forEach(System.out::println);
        }
    }

    public void listEditorFiles() {
        String username = currentUser.getUsername();
        List<String> editorFiles = permissionsManager.getFilesWhereEditor(username);

        if (editorFiles.isEmpty()) {
            System.out.println("You are not an editor of any files.");
        } else {
            System.out.println("Files where you are an editor:");
            editorFiles.forEach(System.out::println);
        }
    }

    public void listAdminFiles() {
        String username = currentUser.getUsername();
        List<String> adminFiles = permissionsManager.getFilesWhereAdmin(username);

        if (adminFiles.isEmpty()) {
            System.out.println("You are not an admin of any files.");
        } else {
            System.out.println("Files where you are admin:");
            adminFiles.forEach(System.out::println);
        }
    }
}

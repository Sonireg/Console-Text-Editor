package editor.User;

import java.util.HashMap;
import java.util.Map;
import editor.User.Permissions.PermissionsManager;;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final PermissionsManager permissionsManager;
    private User currentUser;

    public UserManager(PermissionsManager permissionsManager) {
        this.permissionsManager = permissionsManager;
    }

    public User login(String username) {
        users.putIfAbsent(username, new User(username));
        currentUser = users.get(username);
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
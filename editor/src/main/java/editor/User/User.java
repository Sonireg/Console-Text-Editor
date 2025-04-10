package editor.User;

import java.io.IOException;
import editor.User.Permissions.PermissionsManager;
import editor.User.Roles.*;

public class User {
    private final String username;
    private OpeningRole role = new ViewerRole();
    private final PermissionsManager permissionsManager;

    public User(String username, PermissionsManager permissionsManager) {
        this.username = username;
        this.permissionsManager = permissionsManager;
    }

    public String getUsername() {
        return username;
    }

    public void setOpeningRole(OpeningRole role) {
        this.role = role;
    }

    public void openFile(String fileName) throws IOException {
        if (!role.canAcces(fileName, username, permissionsManager)) {
            System.out.println("Not enough permissions!");
            return;
        }
        role.openFile(fileName);
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + '}';
    }
}

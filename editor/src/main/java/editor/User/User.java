package editor.User;

import java.io.IOException;

import editor.BasicEditor.FileManager;
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

    public String openFile(String fileName) throws IOException {
        if (!role.canAcces(fileName, username, permissionsManager)) {
            return "Not enough permissions!";
        }
        role.openFile(fileName);
        return "";
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + '}';
    }

    public String deleteFile(String fileName) {
        if (!permissionsManager.isAdmin(fileName, username)) {
            return "Not Admin!";
        }
        if (FileManager.deleteFile(fileName)) {
            return "File \"" + fileName + "\" deleted!";
        } else {
            return "Unable to delete \"" + fileName + "\"!";
        }
    }
}

package editor.User.Roles;

import java.io.IOException;

import editor.User.Permissions.PermissionsManager;

public interface OpeningRole {
    public void openFile(String filename) throws IOException;
    public boolean canAcces(String filename, String username, PermissionsManager permissionsManager);
}

package editor.User.Roles;

import java.io.IOException;

import editor.BasicEditor.EditorViewer;
import editor.User.Permissions.FilePermissions;
import editor.User.Permissions.PermissionsManager;


public class EditorRole implements OpeningRole {
    @Override
    public void openFile(String fileName) throws IOException {
        EditorViewer.viewCycle(fileName);
    }

    @Override
    public boolean canAcces(String fileName, String username, PermissionsManager permissionsManager) {
        FilePermissions permissions = permissionsManager.getOrCreatePermissions(fileName, username);
        return permissions.getAdmin().equals(username) || permissions.isEditor(username);
    }
}
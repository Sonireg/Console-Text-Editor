package editor.User.Permissions;

import java.util.HashSet;
import java.util.Set;

public class FilePermissions {
    private String admin;
    private Set<String> editors = new HashSet<>();

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public Set<String> getEditors() {
        return editors;
    }

    public void setEditors(Set<String> editors) {
        this.editors = editors;
    }

    public boolean isEditor(String username) {
        return editors.contains(username);
    }

    public void addEditor(String username) {
        editors.add(username);
    }

    public void removeEditor(String username) {
        editors.remove(username);
    }
}

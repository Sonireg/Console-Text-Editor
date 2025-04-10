package editor.User.Permissions;

import java.util.HashMap;
import java.util.Map;

public class DocumentPermissions {
    private Map<String, RoleType> userRoles = new HashMap<>();

    public Map<String, RoleType> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Map<String, RoleType> userRoles) {
        this.userRoles = userRoles;
    }

    public RoleType getRoleForUser(String username) {
        return userRoles.getOrDefault(username, RoleType.VIEWER);
    }

    public void setRoleForUser(String username, RoleType role) {
        userRoles.put(username, role);
    }
}

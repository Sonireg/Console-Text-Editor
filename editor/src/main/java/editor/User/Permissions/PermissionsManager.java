package editor.User.Permissions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PermissionsManager {
    private static final String PERMISSIONS_FILE = "permissions.json";
    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, DocumentPermissions> permissions = new HashMap<>();

    public PermissionsManager() {
        loadPermissions();
    }

    public void loadPermissions() {
        try {
            File file = new File(PERMISSIONS_FILE);
            if (file.exists()) {
                permissions = mapper.readValue(file,
                        mapper.getTypeFactory().constructMapType(Map.class, String.class, DocumentPermissions.class));
            } else {
                savePermissions();
            }
        } catch (IOException e) {
            System.out.println("Failed to load permissions: " + e.getMessage());
        }
    }

    public void savePermissions() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PERMISSIONS_FILE), permissions);
        } catch (IOException e) {
            System.out.println("Failed to save permissions: " + e.getMessage());
        }
    }

    public DocumentPermissions getPermissionsForDocument(String fileName) {
        return permissions.computeIfAbsent(fileName, k -> new DocumentPermissions());
    }

    public RoleType getUserRoleForDocument(String fileName, String username) {
        return getPermissionsForDocument(fileName).getRoleForUser(username);
    }

    public void setUserRoleForDocument(String fileName, String username, RoleType role) {
        getPermissionsForDocument(fileName).setRoleForUser(username, role);
        savePermissions();
    }

    public Map<String, DocumentPermissions> getAllPermissions() {
        return permissions;
    }
}

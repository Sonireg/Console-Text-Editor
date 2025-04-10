package editor.User.Permissions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PermissionsManager {
    private static final String USERS_FILE = "users.json";
    private static final String PERMISSIONS_FILE = "permissions.json";

    private final Set<String> users = new HashSet<>();
    private final Map<String, FilePermissions> filePermissions = new HashMap<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PermissionsManager() {
        loadUsers();
        loadPermissions();
    }

    // ---------- USERS LOGIC ----------
    public void addUser(String username) {
        if (users.add(username)) saveUsers();
    }

    public boolean userExists(String username) {
        return users.contains(username);
    }

    private void loadUsers() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            String[] loaded = gson.fromJson(reader, String[].class);
            if (loaded != null) Collections.addAll(users, loaded);
        } catch (IOException ignored) {}
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving users.json: " + e.getMessage());
        }
    }

    // ---------- PERMISSIONS LOGIC ----------
    public FilePermissions getOrCreatePermissions(String filename, String adminUsername) {
        if (!filePermissions.containsKey(filename)) {
            FilePermissions perms = new FilePermissions();
            perms.setAdmin(adminUsername);
            filePermissions.put(filename, perms);
            savePermissions();
            return perms;
        }
        return filePermissions.get(filename);
    }

    public void setPermissions(String filename, FilePermissions permissions) {
        filePermissions.put(filename, permissions);
        savePermissions();
    }

    public FilePermissions getPermissions(String filename) {
        return filePermissions.get(filename);
    }

    private void loadPermissions() {
        try (Reader reader = new FileReader(PERMISSIONS_FILE)) {
            Type type = new TypeToken<Map<String, FilePermissions>>(){}.getType();
            Map<String, FilePermissions> loaded = gson.fromJson(reader, type);
            if (loaded != null) filePermissions.putAll(loaded);
        } catch (IOException ignored) {}
    }

    private void savePermissions() {
        try (Writer writer = new FileWriter(PERMISSIONS_FILE)) {
            gson.toJson(filePermissions, writer);
        } catch (IOException e) {
            System.err.println("Error saving permissions.json: " + e.getMessage());
        }
    }

    public boolean isAdmin(String filename, String username) {
        FilePermissions perms = filePermissions.get(filename);
        return perms != null && username.equals(perms.getAdmin());
    }
    
    // Добавить редактора
    public boolean addEditor(String filename, String username) {
        FilePermissions perms = filePermissions.get(filename);
        if (perms == null) return false;
    
        boolean added = perms.getEditors().add(username);
        if (added) savePermissions();
        return added;
    }
    
    // Удалить редактора
    public boolean removeEditor(String filename, String username) {
        FilePermissions perms = filePermissions.get(filename);
        if (perms == null) return false;
    
        boolean removed = perms.getEditors().remove(username);
        if (removed) savePermissions();
        return removed;
    }
    
    // Назначить нового админа
    public boolean setAdmin(String filename, String newAdminUsername) {
        FilePermissions perms = filePermissions.get(filename);
        if (perms == null) return false;
    
        perms.setAdmin(newAdminUsername);
        savePermissions();
        return true;
    }

    

    public Set<String> getAllFilenames() {
        return filePermissions.keySet();
    }
    
    public List<String> getFilesWhereEditor(String username) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, FilePermissions> entry : filePermissions.entrySet()) {
            if (entry.getValue().getEditors().contains(username)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
    
    public List<String> getFilesWhereAdmin(String username) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, FilePermissions> entry : filePermissions.entrySet()) {
            if (username.equals(entry.getValue().getAdmin())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}

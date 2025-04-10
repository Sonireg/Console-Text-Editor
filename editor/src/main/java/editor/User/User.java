package editor.User;
import java.io.IOException;

import editor.User.Roles.*;

public class User {
    private final String username;
    private OpeningRole role = new ViewerRole();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setOpeningRole(OpeningRole role) {
        this.role = role;
    }

    public void openFile(String fileName) throws IOException {
        role.openFile(fileName);
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + '}';
    }
}

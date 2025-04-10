package editor.User.Roles;

import java.io.IOException;

import editor.BasicEditor.EditorViewer;

public class EditorRole implements OpeningRole {
    @Override
    public void openFile(String fileName) throws IOException {
        EditorViewer.viewCycle(fileName);
    }
}
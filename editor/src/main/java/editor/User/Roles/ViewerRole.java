package editor.User.Roles;

import java.io.IOException;

import editor.Parsers.*;
import editor.User.Permissions.PermissionsManager;
import editor.Viewer.FileViewer;

public class ViewerRole implements OpeningRole {
    @Override
    public void openFile(String fileName) throws IOException {
        String[] fileNameParts = fileName.split("\\.");
        String fileFormat = fileNameParts[fileNameParts.length - 1].toLowerCase();
        switch (fileFormat) {
            case "md" -> FileViewer.viewCycle(fileName, new MDParser());
            case "rtf" -> FileViewer.viewCycle(fileName, new RTFParser());
            case "json" -> FileViewer.viewCycle(fileName, new JSONParser());
            case "xml" -> FileViewer.viewCycle(fileName, new XMLParser());
            default -> FileViewer.viewCycle(fileName, new TXTPArser());
        }
    }

    @Override
    public boolean canAcces(String fileName, String username, PermissionsManager permissionsManager) {
        return true;
    }
}
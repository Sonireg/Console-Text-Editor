package editor.User.Roles;

import java.io.IOException;

import editor.Parsers.*;
import editor.Viewer.FileViewer;

public class ViewerRole implements OpeningRole {
    @Override
    public void openFile(String fileName) throws IOException {
        String[] fileNameParts = fileName.split("\\.");
        String fileFormat = fileNameParts[fileNameParts.length - 1].toLowerCase();
        switch (fileFormat) {
            case "md" -> FileViewer.viewCycle(fileName, new MDParser());
            case "rtf" -> FileViewer.viewCycle(fileName, new RTFParser());
            default -> FileViewer.viewCycle(fileName, new TXTPArser());
        }
    }
}
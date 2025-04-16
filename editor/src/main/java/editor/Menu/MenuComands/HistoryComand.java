package editor.Menu.MenuComands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import editor.BasicEditor.Saving.HistoryManager;
import editor.User.UserManager;
import editor.User.Roles.ViewerRole;

public class HistoryComand {
    public static void handle(String[] args, UserManager userManager) {
        if (args.length < 2) {
            System.out.println("Usage: 'history --count' or 'history <filename> <version_number>'");
            return;
        }

        String fileName = args[1];

        if (args.length == 3 && args[2].equals("--count")) {
            int count = HistoryManager.getSnapshotCount(fileName);
            System.out.println("All versions: " + count);
            return;
        }

        if (args.length == 3) {
            try {
                int version = Integer.parseInt(args[2]);
                String content = HistoryManager.getSnapshot(fileName, version);
                if (content == null) {
                    System.out.println("There is no such version!");
                    return;
                }

                // Создаём временный файл
                String ext = getFileExtension(fileName);
                String tempName = fileName.replace("." + ext, "") + "TEMP." + ext;
                Files.write(Path.of(tempName), content.getBytes());

                // Смотрим его как Viewer
                userManager.getCurrentUser().setOpeningRole(new ViewerRole());
                userManager.getCurrentUser().openFile(tempName);

            } catch (NumberFormatException | IOException e) {
                System.out.println("Error while opening history: " + e.getMessage());
            }
        } else {
            System.out.println("Usage: 'history --count' or 'history <filename> <version_number>'");
        }
    }

    
    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1) : "";
    }
}

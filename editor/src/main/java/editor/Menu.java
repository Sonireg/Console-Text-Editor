package editor;

import java.io.IOException;
import java.util.Scanner;

import editor.BasicEditor.Viewer;

public class Menu {
    public static void menuCycle() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            
            String command = scanner.nextLine();
            
    
            String[] args = command.split(" ");
            
            if (args.length == 0) continue;
            if (args[0].equals("open")) {
                Viewer.viewCycle(args[1]);
            }
            else if (args[0].equals("exit")) {
                break;
            }
        }
        scanner.close();
    }
}
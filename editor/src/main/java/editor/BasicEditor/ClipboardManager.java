package editor.BasicEditor;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class ClipboardManager {
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    // Копирование текста в буфер обмена
    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        clipboard.setContents(selection, selection);
    }

    // Получение текста из буфера обмена
    public static String pasteFromClipboard() {
        try {
            Transferable content = clipboard.getContents(null);
            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) content.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | java.io.IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

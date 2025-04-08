package editor.TerminalSettings;

public class TerminalSettings {
    private int fontSize;
    private static TerminalSettings instance = null;
    private TerminalTheme[] themes = new TerminalTheme[]{TerminalTheme.DARK, 
                                                         TerminalTheme.LIGHT, 
                                                         TerminalTheme.GREEN};
    private int currentTheme;

    private TerminalSettings() {
        currentTheme = 0;
        fontSize = 12;
    }

    public static TerminalSettings GetInstance() {
        if (instance == null) TerminalSettings.instance = new TerminalSettings();  
        return instance;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void increaseFontSize() {
        fontSize = Math.min(fontSize + 1, 24);
    }

    public void decreaseFontSize() {
        fontSize = Math.max(fontSize - 1, 6);
    }

    public TerminalTheme getTheme() {
        return themes[currentTheme];
    }

    public void toggleTheme() {
        currentTheme = (currentTheme + 1) % themes.length;
    }
}


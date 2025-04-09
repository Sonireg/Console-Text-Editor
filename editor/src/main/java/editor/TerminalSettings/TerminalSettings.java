package editor.TerminalSettings;

public class TerminalSettings {
    private static TerminalSettings instance = null;
    private TerminalTheme[] themes = new TerminalTheme[]{TerminalTheme.DARK, 
                                                         TerminalTheme.LIGHT, 
                                                         TerminalTheme.GREEN};
    private int currentTheme;

    private TerminalSettings() {
        currentTheme = 0;
    }

    public static TerminalSettings GetInstance() {
        if (instance == null) TerminalSettings.instance = new TerminalSettings();  
        return instance;
    }

    public TerminalTheme getTheme() {
        return themes[currentTheme];
    }

    public void toggleTheme() {
        currentTheme = (currentTheme + 1) % themes.length;
    }
}


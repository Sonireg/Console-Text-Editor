package editor.TerminalSettings;

public class TerminalTheme {
    private final String textColor;
    private final String backgroundColor;

    public TerminalTheme(String textColor, String backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String apply() {
        return textColor + backgroundColor;
    }

    public static final TerminalTheme LIGHT = new TerminalTheme("\u001B[30m", "\u001B[47m"); // черный на белом
    public static final TerminalTheme DARK = new TerminalTheme("\u001B[37m", "\u001B[40m");
    public static final TerminalTheme GREEN = new TerminalTheme("\u001B[32m", "\u001B[40m");  // зелёный на чёрном
}
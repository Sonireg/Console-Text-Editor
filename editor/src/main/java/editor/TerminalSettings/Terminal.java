package editor.TerminalSettings;

public interface Terminal {
    void enableRawMode();

    void disableRawMode();

    public TerminalSize getTerminalSize();
}


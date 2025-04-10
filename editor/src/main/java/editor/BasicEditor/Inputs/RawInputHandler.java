package editor.BasicEditor.Inputs;

import java.io.IOException;

public class RawInputHandler {
    private static final int SHIFT = 49;

    public int handleInput() throws IOException {
        int key = readKey();
        return key;
    }

    private int readKey() throws IOException {
        int key = System.in.read();
        if (key != '\033') return key;
        key = System.in.read();
        int nextKey = System.in.read();
        if (nextKey == SHIFT) {
            key = System.in.read();
            key = System.in.read();
            key = System.in.read();
            return key+1000;
        }
        
        if (nextKey != '[' && nextKey != 'O') return nextKey;

        int yetAnotherKey = System.in.read();
        return yetAnotherKey;
    }

}
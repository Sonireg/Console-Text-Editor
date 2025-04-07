package editor;

import java.io.IOException;

public class InputHandler {


    public int handleInput() throws IOException {
        int key = readKey();
        return key;
    }

    private int readKey() throws IOException {
        int key = System.in.read();
        if (key != '\033') return key;

        int nextKey = System.in.read();
        if (nextKey != '[' && nextKey != 'O') return nextKey;

        int yetAnotherKey = System.in.read();
        return yetAnotherKey;
    }

}
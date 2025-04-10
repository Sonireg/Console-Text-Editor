package editor.BasicEditor.Saving;

import java.util.Objects;

public class FromToKey {
    SaveOptions.Format from, to;

    FromToKey(SaveOptions.Format from, SaveOptions.Format to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FromToKey that)) return false;
        return from == that.from && to == that.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}

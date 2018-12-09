package io.molr.gui.fx.widgets.progress;

import io.molr.commons.domain.Result;

import java.util.Objects;

import static io.molr.commons.domain.Result.UNDEFINED;

public class Progress {

    private final double value;
    private final String text;
    private final Result result;

    public Progress(double value, String text, Result result) {
        this.value = value;
        this.text = text;
        this.result = result;
    }

    public static Progress undefined() {
        return new Progress(0.0, "", UNDEFINED);
    }

    public double value() {
        return value;
    }

    public String text() {
        return text;
    }

    public Result result() {
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Progress progress = (Progress) o;
        return Double.compare(progress.value, value) == 0 &&
                Objects.equals(text, progress.text) &&
                result == progress.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, text, result);
    }

    @Override
    public String toString() {
        return "Progress{" +
                "value=" + value +
                ", text='" + text + '\'' +
                ", result=" + result +
                '}';
    }
}

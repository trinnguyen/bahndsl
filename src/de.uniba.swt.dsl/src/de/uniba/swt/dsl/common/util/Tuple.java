package de.uniba.swt.dsl.common.util;

import java.util.Objects;

public class Tuple<F, S> {
    private F first;
    private S second;

    public static <L,R> Tuple<L,R> of(L first, R second) {
        return new Tuple<>(first, second);
    }

    Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Tuple() {
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (!Objects.equals(first, tuple.first)) return false;
        return Objects.equals(second, tuple.second);
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}

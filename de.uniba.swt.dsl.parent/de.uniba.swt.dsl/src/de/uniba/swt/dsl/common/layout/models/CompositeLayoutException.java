package de.uniba.swt.dsl.common.layout.models;

import java.util.List;
import java.util.stream.Collectors;

public class CompositeLayoutException extends Exception {
    private List<LayoutException> exceptions;

    public CompositeLayoutException(String message) {
        this(List.of(new LayoutException(message)));
    }

    public CompositeLayoutException(List<LayoutException> exceptions) {
        super(exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining("\n")), exceptions.get(0));
        this.exceptions = exceptions;
    }

    public List<LayoutException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<LayoutException> exceptions) {
        this.exceptions = exceptions;
    }
}

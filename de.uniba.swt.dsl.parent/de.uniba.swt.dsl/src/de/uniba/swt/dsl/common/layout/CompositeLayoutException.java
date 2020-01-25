package de.uniba.swt.dsl.common.layout;

import java.util.List;
import java.util.stream.Collectors;

public class CompositeLayoutException extends Exception {
    private List<LayoutException> exceptions;

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

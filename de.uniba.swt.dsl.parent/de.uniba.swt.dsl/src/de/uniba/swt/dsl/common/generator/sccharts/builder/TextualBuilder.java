package de.uniba.swt.dsl.common.generator.sccharts.builder;

import java.util.ArrayList;
import java.util.List;

public class TextualBuilder {

    public final static String LINE_BREAK = System.lineSeparator();

    private List<String> builder;

    public TextualBuilder() {
        builder = new ArrayList<>();
    }

    public TextualBuilder append(String text) {
        builder.add(text);
        return this;
    }

    public TextualBuilder appendLine(String text) {
        builder.add(text);
        builder.add(LINE_BREAK);
        return this;
    }

    public TextualBuilder clear() {
        builder.clear();
        return this;
    }

    public String build() {
        return String.join(" ", builder);
    }
}

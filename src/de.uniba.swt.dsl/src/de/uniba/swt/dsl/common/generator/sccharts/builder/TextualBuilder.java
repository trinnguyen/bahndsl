package de.uniba.swt.dsl.common.generator.sccharts.builder;

public class TextualBuilder {

    public final static String LINE_BREAK = System.lineSeparator();

    private final static String SPACE = " ";

    private StringBuilder builder = new StringBuilder();

    private int indentLevel = 0;

    private String linePrefix = "";

    public TextualBuilder increaseIndent() {
        indentLevel++;
        updatePrefix();
        return this;
    }

    public TextualBuilder decreaseIndent() {
        indentLevel = Math.max(0, indentLevel - 1);
        updatePrefix();
        return this;
    }

    public TextualBuilder append(String text) {
        builder.append(text).append(SPACE);
        return this;
    }

    public TextualBuilder appendLine(String text) {
        builder.append(LINE_BREAK).append(linePrefix).append(text);
        return this;
    }

    public TextualBuilder clear() {
        builder = new StringBuilder();
        return this;
    }

    public String build() {
        return builder.toString();
    }

    private void updatePrefix() {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            tmp.append(SPACE).append(SPACE);
        }
        linePrefix = tmp.toString();
    }
}

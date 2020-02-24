package de.uniba.swt.dsl.common.util;

import java.util.LinkedList;
import java.util.List;

public class YamlExporter {

    private static final String SPACE = "  ";
    protected List<String> items = new LinkedList<>();
    protected int indentLevel;

    protected void reset() {
        items.clear();
        indentLevel = 0;
    }

    public void appendLine(String text, Object... args) {
        items.add(SPACE.repeat(Math.max(0, indentLevel)) + String.format(text, args));
    }

    public void increaseLevel()
    {
        indentLevel++;
    }

    public void decreaseLevel()
    {
        indentLevel--;
    }

    protected String build()
    {
        return String.join("\n", items);
    }

    @Override
    public String toString() {
        return build();
    }
}

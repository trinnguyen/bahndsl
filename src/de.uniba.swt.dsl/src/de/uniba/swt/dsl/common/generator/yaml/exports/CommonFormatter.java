package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.Length;
import de.uniba.swt.dsl.bahn.Weight;

class CommonFormatter {
    public static String formatWeight(Weight weight) {
        return String.format("%s%s", weight.getValue(), weight.getUnit().toString().toLowerCase());
    }

    public static String formatLength(Length length) {
        return String.format("%s%s", length.getValue(), length.getUnit().toString().toLowerCase());
    }
}

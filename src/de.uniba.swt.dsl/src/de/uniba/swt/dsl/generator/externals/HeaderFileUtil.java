package de.uniba.swt.dsl.generator.externals;

import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HeaderFileUtil {

    private static final Logger logger = Logger.getLogger(HeaderFileUtil.class);

    public static void updateThreadStatus(IFileSystemAccess2 fsa, String filename, String oldPrefix, String oldSuffix, String newName) {
        var oldName = oldPrefix + oldSuffix;

        // find the typedef enum and remove
        var lines = Arrays.asList(fsa.readTextFile(filename).toString().split(System.lineSeparator()));
        var end = findEnumNameLine(oldName, lines);
        if (end >= 0) {
            var start = findTypeDefLine(lines, end);
            if (start >= 0) {
                // remove
                var newLines = removeAndReplaceLines(lines, start, end, oldName, newName);

                // write to file
                fsa.generateFile(filename, mergeString(newLines));
            }
        }
    }

    private static CharSequence mergeString(List<String> newLines) {
        return String.join(System.lineSeparator(), newLines);
    }

    private static List<String> removeAndReplaceLines(List<String> lines, int start, int end, String oldName, String newName) {
        List<String> newLines = new ArrayList<>();
        newLines.add("#include \"tick_wrapper.h\"");
        for (int i = 0; i < lines.size(); i++) {
            if (i >= start && i<= end) {
                continue;
            }

            // replace
            newLines.add(lines.get(i).replace(oldName, newName));
        }

        return newLines;
    }

    private static int findTypeDefLine(List<String> lines, int end) {
        var regex = "^\\s*typedef\\s*enum\\s*";
        var pattern = Pattern.compile(regex);
        for (int i = end; i >= 0; i--) {
            if (pattern.matcher(lines.get(i)).find()) {
                // remove comment if needed
                if (i - 1 >= 0 && lines.get(i - 1).startsWith("//")) {
                    return i -1;
                }

                return i;
            }
        }

        return -1;
    }

    private static int findEnumNameLine(String name, List<String> lines) {
        var regex = String.format("^\\s*}\\s*%s\\s*;", name);
        var pattern = Pattern.compile(regex);
        for (int i = 0; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).find()) {
                return i;
            }
        }

        return -1;
    }
}

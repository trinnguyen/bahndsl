package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryExternalGenerator extends AbstractExternalGenerator {
    private static Logger logger = Logger.getLogger(LibraryExternalGenerator.class);
    private List<Tuple<String, String>> resources;

    /**
     * add built-in resources
     * @param resources resources
     */
    public void setResources(List<Tuple<String, String>> resources) {
        this.resources = resources;
    }

    @Override
    protected String[] supportedTools() {
        return new String[] {"cc", "clang", "gcc"};
    }

    @Override
    public boolean generate(String outputPath) {
        // list all c files in the folder
        List<String> fileNames = null;
        try {
            fileNames =  Files.walk(Paths.get(outputPath))
                    .map(p -> p.getFileName().toString())
                    .filter(f -> f.endsWith(".c")).collect(Collectors.toList());
        } catch (IOException e) {
            logger.warn(e);
            return false;
        }

        // generate temporary files
        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("bahn");
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }

        List<String> tmpFiles = generateTempResources(tmpDir.toString());
        if (tmpFiles != null) {
            logger.debug(String.format("Copied resource files to: %s", String.join(", ", tmpFiles)));
            fileNames.addAll(tmpFiles);
        } else {
            logger.warn("Failed to copy resource files to temp folder");
        }

        // start code generation
        var out = "request_route." + getOsLibExt();
        List<String> args = new ArrayList<>();
        args.add("-shared");
        args.add("-undefined");
        args.add("dynamic_lookup");
        args.addAll(fileNames.stream().filter(f -> f.endsWith(".c")).collect(Collectors.toList()));
        args.add("-I.");
        args.add("-I" + tmpDir);
        args.add("-o");
        args.add(out);
        var res = executeArgs(args.toArray(new String[0]), outputPath);

        // clean
        cleanTemp(tmpDir, tmpFiles);
        return res;
    }

    private void cleanTemp(Path tmpDir, List<String> tmpFiles) {
        if (tmpFiles != null) {
            try {
                for (String tmpFile : tmpFiles) {
                    Files.delete(Path.of(tmpFile));
                }
                Files.delete(tmpDir);
            } catch (IOException e) {
                logger.debug("Error deleting temp file: " + e.getMessage());
            }
        }
    }

    private List<String> generateTempResources(String tmpDir) {
        if (resources != null) {
            try {
                List<String> result = new ArrayList<>();
                for (Tuple<String, String> resource : resources) {
                    try (var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(resource.getFirst())) {
                        if (stream != null) {
                            var target = Paths.get(tmpDir, resource.getSecond());
                            var res = Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
                            if (res > 0) {
                                result.add(target.toString());
                            }
                        }
                    }
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getOsLibExt() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return "dylib";

        if (os.contains("win"))
            return "dll";

        return "so";
    }
}

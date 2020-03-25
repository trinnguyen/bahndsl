package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.BahnConstants;
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

public class LibraryExternalGenerator extends ExternalGenerator {
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
    protected boolean execute(String outputPath) {
        // list all c files in the folder
        List<String> fileNames = new ArrayList<>();
        fileNames.add(BahnConstants.REQUEST_ROUTE_FUNC_NAME + ".c");

        // generate temporary files
        Path tmpDir = Path.of(outputPath); //getTempDir();
        if (tmpDir == null) {
            logger.warn("Failed to prepare resource for generating dynamic library");
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
        List<String> args = new ArrayList<>();
        args.add("-shared");
        args.add("-undefined");
        args.add("dynamic_lookup");
        args.addAll(fileNames.stream().filter(f -> f.endsWith(".c")).collect(Collectors.toList()));
        args.add("-I.");
        args.add("-I" + tmpDir);
        args.add("-o");
        args.add(getOutputFileName());
        var res = executeArgs(args.toArray(new String[0]), outputPath);

        // clean
        //cleanTemp(tmpDir, tmpFiles);
        return res;
    }

    private static Path getTempDir() {
        try {
            return Files.createTempDirectory("bahn");
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[] { getOutputFileName() };
    }

    private void cleanTemp(Path tmpDir, List<String> tmpFiles) {
        if (tmpFiles != null) {
            try {
                for (String tmpFile : tmpFiles) {
                    Files.delete(Path.of(tmpFile));
                }

                var file = tmpDir.toFile();
                if (file.isDirectory() && file.list() == null) {
                    file.delete();
                }
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

    private static String getOutputFileName() {
        return "libinterlocking_bahndslfilename." + getOsLibExt();
    }

    private static String getOsLibExt() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return "dylib";

        if (os.contains("win"))
            return "dll";

        return "so";
    }
}

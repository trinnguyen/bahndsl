package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryExternalGenerator extends ExternalGenerator {

    private static final String ThreadStatusName = "ThreadStatus";

    private static final String WrapperThreadStatusName = "wrapper_thread_status";

    private static final String TemporaryObjFolderName = "obj";

    private static final String SharedLibNamePrefix = "libinterlocking";

    private static final Logger logger = Logger.getLogger(LibraryExternalGenerator.class);

    private final List<Tuple<String, String>> resources;

    private String sourceFileName;

    public LibraryExternalGenerator() {
        resources = List.of(
                Tuple.of("tick_wrapper_header.code", "tick_wrapper.h"),
                Tuple.of("tick_wrapper.code", "tick_wrapper.c"),
                Tuple.of("bahn_data_util.code", "bahn_data_util.h"));
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    @Override
    protected String[] supportedTools() {
        return new String[] {"cc", "clang", "gcc"};
    }

    @Override
    protected boolean execute(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {

        var genModels = new String[]{BahnConstants.REQUEST_ROUTE_FUNC_NAME, BahnConstants.DRIVE_ROUTE_FUNC_NAME};

        // process header
        preprocessHeaders(fsa, genModels);

        // list all c files in the folder
        List<String> fileNames = new ArrayList<>();
        for (String genModel : genModels) {
            fileNames.add(genModel + ".c");
        }

        // generate temporary files
        List<String> tmpFiles = generateTempResources(fsa);
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
        args.add("-I" + TemporaryObjFolderName);
        args.add("-o");
        args.add(getOutputFileName());
        var res = executeArgs(args.toArray(new String[0]), fsa, runtimeExec);

        // delete temporary files if needed
         cleanTemp(fsa, tmpFiles);

        return res;
    }

    private void preprocessHeaders(IFileSystemAccess2 fsa, String[] genModels) {
        for (String genModel : genModels) {
            logger.debug(String.format("Process tick header for: %s", genModel));
            var oldPrefix = BahnUtil.generateLogicNaming(genModel);
            HeaderFileUtil.updateThreadStatus(fsa, genModel + ".h", oldPrefix, ThreadStatusName, WrapperThreadStatusName);
        }
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[] { getOutputFileName() };
    }

    private List<String> generateTempResources(IFileSystemAccess2 fsa) {
        try {
            List<String> result = new ArrayList<>();
            for (Tuple<String, String> resource : resources) {
                try (var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(resource.getFirst())) {
                    if (stream != null) {
                        var name = Path.of(TemporaryObjFolderName, resource.getSecond()).toString();
                        fsa.generateFile(name, stream);
                        result.add(name);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            logger.warn("Failed to generate temporary resources", e);
        }

        return null;
    }

    private String getOutputFileName() {
        return String.format("%s_%s.%s", SharedLibNamePrefix, sourceFileName, getOsLibExt());
    }

    private static String getOsLibExt() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return "dylib";

        if (os.contains("win"))
            return "dll";

        return "so";
    }

    private static void cleanTemp(IFileSystemAccess2 fsa, List<String> tmpFiles) {
        if (tmpFiles != null) {
            for (String tmpFile : tmpFiles) {
                fsa.deleteFile(tmpFile);
            }
        }

        fsa.deleteFile(TemporaryObjFolderName);
    }
}

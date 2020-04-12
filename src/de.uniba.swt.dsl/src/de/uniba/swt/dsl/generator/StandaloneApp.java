package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.generator.externals.LibraryExternalGenerator;
import de.uniba.swt.dsl.generator.externals.LowLevelCodeExternalGenerator;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class StandaloneApp {

    public static final String MODE_DEFAULT = "default";

    public static final String MODE_C_CODE = "c-code";

    public static final String MODE_LIBRARY = "library";

    private static Logger logger = Logger.getLogger(StandaloneApp.class);

    @Inject
    private Provider<ResourceSet> resourceSetProvider;

    @Inject
    private IResourceValidator validator;

    @Inject
    private GeneratorDelegate generator;

    @Inject
    private JavaIoFileSystemAccess fileAccess;

    @Inject
    private LowLevelCodeExternalGenerator lowLevelCodeExternalGenerator;

    @Inject
    private LibraryExternalGenerator libraryGenerator;

    public boolean runGenerator(String filePath, String outputPath, String mode) {

        // validate
        if (filePath == null) {
            System.err.println("No input file");
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Input file is not exist: " + file.toString());
            return false;
        }

        // load output
        if (outputPath == null || outputPath.isEmpty())
            outputPath = Paths.get(file.getAbsoluteFile().getParent(), "src-gen").toAbsolutePath().toString();

        // Load the resource
        Resource resource = resourceSetProvider.get().getResource(URI.createFileURI(filePath), true);

        // Validate the resource
        List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
        if (!list.isEmpty()) {
            boolean anyError = false;
            for (Issue issue : list) {
                if (issue.getSeverity() == Severity.ERROR) {
                    System.err.println(issue);
                    anyError = true;
                } else {
                    System.out.println(issue);
                }

            }

            // stop
            if (anyError) {
                return false;
            }
        }

        logger.info(String.format("Code generation mode: %s", mode));

        // Configure and start the generator
        logger.info("Start generating network layout and SCCharts models");
        if (generate(resource, outputPath, mode, file)) {
            System.out.println(String.format("Code generation finished: %s", outputPath));
            return true;
        }

        return false;
    }

    private boolean generate(Resource resource, String outputPath, String mode, File file) {
        // prepare
        fileAccess.setOutputPath(outputPath);
        GeneratorContext context = new GeneratorContext();
        context.setCancelIndicator(CancelIndicator.NullImpl);

        // step 1: generate default artifacts
        generator.generate(resource, fileAccess, context);

        // step 2: generate low-level C-Code and dynamic library
        boolean genLibrary = MODE_LIBRARY.equalsIgnoreCase(mode);
        boolean genLowLevelCode = genLibrary || MODE_C_CODE.equalsIgnoreCase(mode);

        if (genLowLevelCode) {
            logger.info("Start generating low-level code");
            if (!lowLevelCodeExternalGenerator.generate(outputPath))
                return false;
        }

        if (genLibrary) {
            libraryGenerator.setSourceFileName(BahnUtil.getNameWithoutExtension(file.getName()));
            logger.info("Start generating dynamic library");
            return libraryGenerator.generate(outputPath);
        }

        return true;
    }
}

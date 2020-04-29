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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StandaloneApp {

    public static final String MODE_DEFAULT = "default";

    public static final String MODE_C_CODE = "c-code";

    public static final String MODE_LIBRARY = "library";

    private static final Logger logger = Logger.getLogger(StandaloneApp.class);

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

        // Load the resource
        var file = new File(filePath);
        Resource resource = loadResource(file.getAbsolutePath());
        if (resource == null) {
            System.err.println("Invalid input file: " + filePath);
            return false;
        }

        // load output
        if (outputPath == null || outputPath.isEmpty()) {
            outputPath = Paths.get(file.getAbsoluteFile().getParent(), "src-gen").toAbsolutePath().toString();
        }

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
        if (generate(resource, outputPath, mode, file.getName())) {
            System.out.println(String.format("Code generation finished: %s", outputPath));
            return true;
        }

        return false;
    }

    private boolean generate(Resource resource, String outputPath, String mode, String fileName) {
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
            if (!lowLevelCodeExternalGenerator.generate(fileAccess))
                return false;
        }

        if (genLibrary) {
            libraryGenerator.setSourceFileName(BahnUtil.getNameWithoutExtension(fileName));
            logger.info("Start generating dynamic library");
            return libraryGenerator.generate(fileAccess);
        }

        return true;
    }

    private Resource loadResource(String filePath) {
        // validate
        if (filePath == null || !Files.exists(Paths.get(filePath))) {
            return null;
        }

        return resourceSetProvider.get().getResource(URI.createFileURI(filePath), true);
    }
}

/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.internal.util.$Nullable;
import de.uniba.swt.dsl.BahnStandaloneSetup;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			showHelp();
			return;
		}
		
		Injector injector = new BahnStandaloneSetup().createInjectorAndDoEMFRegistration();
		Main main = injector.getInstance(Main.class);

		String outputPath = args.length > 1 ? args[1] : null;
		boolean success = main.runGenerator(args[0], outputPath);
		if (!success) {
			System.exit(1);
		}
	}

	private static void showHelp() {
		String builder = "OVERVIEW: Bahn compiler\n\n" +
				"USAGE: bahnc file [output]\n\n" +
				"EXAMPLE: \n" +
				"\tbahnc example.bahn\n" +
				"\tbahnc example.bahn output/src-gen\n";
		System.out.println(builder);
	}

	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private GeneratorDelegate generator;

	@Inject 
	private JavaIoFileSystemAccess fileAccess;

	protected boolean runGenerator(String filePath, String outputPath) {
		// load output
		File file = new File(filePath);
		if (outputPath == null || outputPath.isEmpty())
			outputPath = Paths.get(file.getAbsoluteFile().getParent(), "src-gen").toAbsolutePath().toString();

		// Load the resource
		ResourceSet set = resourceSetProvider.get();
		Resource resource = set.getResource(URI.createFileURI(filePath), true);

		// Validate the resource
		List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if (!list.isEmpty()) {
			boolean anyError = false;
			for (Issue issue : list) {
				anyError = issue.getSeverity() == Severity.ERROR;
				System.err.println(issue);
			}

			// stop
			if (anyError) {
				return false;
			}
		}

		// Configure and start the generator
		fileAccess.setOutputPath(outputPath);
		GeneratorContext context = new GeneratorContext();
		context.setCancelIndicator(CancelIndicator.NullImpl);
		generator.generate(resource, fileAccess, context);

		System.out.println(String.format("Code generation finished: %s", outputPath));
		return true;
	}
}

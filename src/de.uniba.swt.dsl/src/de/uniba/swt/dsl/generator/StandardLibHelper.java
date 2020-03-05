package de.uniba.swt.dsl.generator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;

public class StandardLibHelper {

    private static final String FILE_NAME = "standardlib.bahn";

    public static boolean loadStandardLibResource(ResourceSet resourceSet) {
        URI uri = URI.createURI(FILE_NAME);
        var resource = resourceSet.createResource(uri);

        var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(FILE_NAME);
        try {
            resource.load(stream, resourceSet.getLoadOptions());
            if (resource.getErrors().size() > 0) {
                System.err.println("Error on parsing built-in standard library");
                for (Resource.Diagnostic error : resource.getErrors()) {
                    System.err.println(error);
                }
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error on loading built-in standing library: " + e.getMessage());
            return false;
        }

        return true;
    }
}

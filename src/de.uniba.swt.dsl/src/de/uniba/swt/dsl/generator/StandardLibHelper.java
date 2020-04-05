package de.uniba.swt.dsl.generator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;

public class StandardLibHelper {
    private static Logger logger = Logger.getLogger(StandardLibHelper.class);

    public static final String FILE_NAME = "standardlib.bahn";

    public static boolean loadStandardLibResource(ResourceSet resourceSet) {
        logger.debug("Start loading standard resource");
        URI uri = URI.createURI(FILE_NAME);
        var resource = resourceSet.createResource(uri);

        var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(FILE_NAME);
        try {
            resource.load(stream, resourceSet.getLoadOptions());
            if (resource.getErrors().size() > 0) {
                logger.error("Error on parsing built-in standard library");
                for (Resource.Diagnostic error : resource.getErrors()) {
                    logger.error(error);
                }
                return false;
            }
        } catch (IOException e) {
            logger.error("Error on loading built-in standing library: " + e.getMessage());
            return false;
        }

        logger.debug("Success loading standard library");
        return true;
    }
}

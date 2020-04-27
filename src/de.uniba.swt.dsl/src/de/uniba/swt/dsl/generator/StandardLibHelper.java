package de.uniba.swt.dsl.generator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StandardLibHelper {
    private static Logger logger = Logger.getLogger(StandardLibHelper.class);

    private static final String RESOURCES_FOLDER_NAME = "resources";
    
    public static final String FILE_NAME = "standardlib.bahn";

	public static void loadStandardLibResource(ResourceSet resourceSet) {
        logger.debug("Start loading standard resource");
        
        URI uri = URI.createURI(FILE_NAME);
        var resource = resourceSet.createResource(uri);

        InputStream stream = null;
        try {
        	// load stream
            stream = loadEmbeddedStream();
            if (stream == null ) {
            	stream = loadPluginResourceStream();
            }
            
            resource.load(stream, resourceSet.getLoadOptions());
            if (resource.getErrors().size() > 0) {
                logger.error("Error on parsing built-in standard library");
                for (Resource.Diagnostic error : resource.getErrors()) {
                    logger.error(error);
                }
            } else {
            	logger.info("Success loading standard library");
            }
        } catch (IOException e) {
            logger.error("Error on loading built-in standing library: " + e.getMessage());
        } finally {
        	if (stream != null) {
        		try {
					stream.close();
				} catch (IOException e) {
					logger.error("Failed to close stream: " + e.getMessage());
				}
        	}
        }
    }
    
    /**
     * Load stream from embedded resource of Java application
     * Used in cli compiler and ide server for visual studio code extension
     * @return
     */
    private static InputStream loadEmbeddedStream() {
    	return StandardLibHelper.class.getClassLoader().getResourceAsStream(FILE_NAME);
	}

    /**
     * Load stream from plugin resource
     * @return
     * @throws IOException
     */
	private static InputStream loadPluginResourceStream() throws IOException  {
    	var url = new URL("platform:/plugin/de.uniba.swt.dsl/" + RESOURCES_FOLDER_NAME + "/" + FILE_NAME);
	    return url.openConnection().getInputStream();
    }
}

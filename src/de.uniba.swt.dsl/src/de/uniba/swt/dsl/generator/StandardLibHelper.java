package de.uniba.swt.dsl.generator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class StandardLibHelper {
    private static Logger logger = Logger.getLogger(StandardLibHelper.class);

    private static final String RESOURCES_FOLDER_NAME = "resources";
    
    public static final String FILE_NAME = "standardlib.bahn";

	public static void loadStandardLibResource(ResourceSet resourceSet) {
        logger.debug("Start loading standard resource");
        
        var resource = loadEmbeddedResource(resourceSet);
        if (resource == null ) {
        	logger.debug("Failed to load resource from embedded, attempt to load from plugin");
        	resource = loadPluginResource(resourceSet);
        }
        
        if (resource != null) {        
	        if (resource.getErrors().size() > 0) {
	            logger.error("Error on parsing built-in standard library");
	            for (Resource.Diagnostic error : resource.getErrors()) {
	                logger.error(error);
	            }
	        } else {
	        	logger.info("Success loading standard library");
	        }
        }
    }
	
	/**
     * Load stream from embedded resource of Java application
     * Used in cli compiler and ide server for visual studio code extension
     * @return
     */
	private static Resource loadEmbeddedResource(ResourceSet resourceSet) {
		try (var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
			if (stream == null)
				return null;
			
			URI uri = URI.createURI(FILE_NAME);
	        var resource = resourceSet.createResource(uri);
			resource.load(stream, resourceSet.getLoadOptions());
			return resource;
		} catch (IOException e) {
			logger.error("Failed to load embedded stream: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Load resource from URI using Eclipse plugin
	 * Run with Eclipse-based IDE only
	 * @param resourceSet
	 * @return
	 */
	private static Resource loadPluginResource(ResourceSet resourceSet)  {
		URI uri = URI.createURI("platform:/plugin/de.uniba.swt.dsl/" + RESOURCES_FOLDER_NAME + "/" + FILE_NAME);
    	return resourceSet.getResource(uri, true);
    }
}

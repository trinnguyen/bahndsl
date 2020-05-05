/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.generator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class StandardLibHelper {
	private StandardLibHelper() {

	}

    private static final Logger logger = Logger.getLogger(StandardLibHelper.class);

    private static final String RESOURCES_FOLDER_NAME = "resources";
    
    public static final String FILE_NAME = "standardlib.bahn";

	public static void loadStandardLibResource(IResourceValidator validator, ResourceSet resourceSet) {
        logger.debug("Start loading standard resource");

		var resource = loadPluginResource(resourceSet);
		logger.debug("Failed to load resource from plugin. Attempt to load from embedded resource");
        if (resource == null ) {
			resource = loadEmbeddedResource(resourceSet);
        }
        
        if (resource != null) {
			List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
	        if (issues != null && issues.size() > 0) {
	            logger.error("Error on parsing built-in standard library");
	            for (var error : issues) {
	                logger.error(error);
	            }
	        } else {
	        	logger.debug("Success loading standard library");
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
    	return resourceSet.getResource(uri, false);
    }
}

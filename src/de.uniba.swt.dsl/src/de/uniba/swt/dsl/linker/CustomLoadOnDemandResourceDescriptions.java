package de.uniba.swt.dsl.linker;

import com.google.inject.Inject;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.scoping.impl.LoadOnDemandResourceDescriptions;

import java.util.Collection;

public class CustomLoadOnDemandResourceDescriptions extends LoadOnDemandResourceDescriptions {

    private Resource ctx;

    @Inject
    private IResourceServiceProvider.Registry registry;

    @Override
    public void initialize(IResourceDescriptions delegate, Collection<URI> validUris, Resource context) {
        super.initialize(delegate, validUris, context);
        this.ctx = context;
    }

    @Override
    public IResourceDescription getResourceDescription(URI uri) {
        // load from resource
        if (uri == StandardLibHelper.getStandardLibPlatformUri()) {
            Resource resource = tryLoadStandardLib();
            if (resource != null) {
                IResourceServiceProvider serviceProvider = registry.getResourceServiceProvider(uri);
                if (serviceProvider==null)
                    throw new IllegalStateException("No "+IResourceServiceProvider.class.getSimpleName()+" found in registry for uri "+uri);
                final IResourceDescription.Manager resourceDescriptionManager = serviceProvider.getResourceDescriptionManager();
                if (resourceDescriptionManager == null)
                    throw new IllegalStateException("No "+IResourceDescription.Manager.class.getName()+" provided by service provider for URI "+uri);
                return resourceDescriptionManager.getResourceDescription(resource);
            }
        }

        return super.getResourceDescription(uri);
    }

    private Resource tryLoadStandardLib() {
        Resource resource = StandardLibHelper.loadPluginResource(ctx.getResourceSet());
        if (resource != null)
            return resource;

        return StandardLibHelper.loadEmbeddedResource(ctx.getResourceSet());
    }
}

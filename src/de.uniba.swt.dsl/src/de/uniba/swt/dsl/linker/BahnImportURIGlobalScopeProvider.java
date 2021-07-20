package de.uniba.swt.dsl.linker;

import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import java.util.LinkedHashSet;

public class BahnImportURIGlobalScopeProvider extends ImportUriGlobalScopeProvider {

    @Override
    protected LinkedHashSet<URI> getImportedUris(Resource resource) {
        LinkedHashSet<URI> set = super.getImportedUris(resource);
        var uri = StandardLibHelper.getStandardLibPlatformUri();
        set.add(uri);

        if (resource.getResourceSet() instanceof XtextResourceSet) {
            ((XtextResourceSet) resource.getResourceSet()).setClasspathUriResolver(new CustomClassPathUriResolver());
        }
        return set;
    }
}

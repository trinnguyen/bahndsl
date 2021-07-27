package de.uniba.swt.dsl.linker;

import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import java.util.LinkedHashSet;

public class BahnImportURIGlobalScopeProvider extends ImportUriGlobalScopeProvider {

    @Override
    protected LinkedHashSet<URI> getImportedUris(Resource resource) {
        BahnImportURIGlobalScopeProvider.setXtextResourceSetOptions(resource);

        LinkedHashSet<URI> set = super.getImportedUris(resource);
        var uri = StandardLibHelper.getStandardLibPlatformUri();
        set.add(uri);

        return set;
    }

    public static void setXtextResourceSetOptions(Resource resource) {
        if (resource.getResourceSet() instanceof XtextResourceSet) {
            XtextResourceSet resourceSet = (XtextResourceSet) resource.getResourceSet();
            resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
            resourceSet.setClasspathUriResolver(new CustomClassPathUriResolver());
        }
    }
}

package de.uniba.swt.dsl.linker;

import com.google.common.base.Predicate;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import java.util.LinkedHashSet;

public class BahnImportURIGlobalScopeProvider extends ImportUriGlobalScopeProvider {

    @Override
    protected LinkedHashSet<URI> getImportedUris(Resource resource) {
        LinkedHashSet<URI> set = super.getImportedUris(resource);
        set.add(StandardLibHelper.getStandardLibPlatformUri());
        return set;
    }

    @Override
    protected IScope createLazyResourceScope(IScope parent, URI uri, IResourceDescriptions descriptions, EClass type, Predicate<IEObjectDescription> filter, boolean ignoreCase) {
        return super.createLazyResourceScope(parent, uri, descriptions, type, filter, ignoreCase);
    }
}

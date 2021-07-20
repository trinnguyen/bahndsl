package de.uniba.swt.dsl.linker;

import de.uniba.swt.dsl.generator.StandaloneApp;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.ClassloaderClasspathUriResolver;

public class CustomClassPathUriResolver extends ClassloaderClasspathUriResolver {
    @Override
    public URI resolve(Object context, URI classpathUri) {
        var ctx = context != null ? context : StandaloneApp.class.getClassLoader();
        return super.resolve(ctx, classpathUri);
    }
}

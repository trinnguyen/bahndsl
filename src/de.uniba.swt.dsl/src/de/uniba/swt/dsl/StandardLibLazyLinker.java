package de.uniba.swt.dsl;

import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;

public class StandardLibLazyLinker extends LazyLinker {
    private static Logger logger = Logger.getLogger(StandardLibLazyLinker.class);
    @Override
    protected void beforeModelLinked(EObject model, IDiagnosticConsumer diagnosticsConsumer) {
        super.beforeModelLinked(model, diagnosticsConsumer);
        var set = model.eResource().getResourceSet();
        if (set.getResources().size() < 2 && !model.eResource().getURI().toString().endsWith(StandardLibHelper.FILE_NAME)) {
            StandardLibHelper.loadStandardLibResource(set);
        }
    }
}

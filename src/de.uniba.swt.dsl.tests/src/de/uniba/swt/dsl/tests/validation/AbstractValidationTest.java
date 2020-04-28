package de.uniba.swt.dsl.tests.validation;

import com.google.inject.Provider;
import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.util.ParseHelper;

public abstract class AbstractValidationTest {

    protected BahnModel internalParse(String src) {
        var set = getResourceSetProvider().get();
        try {
            return getParseHelper().parse(src, set);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract ParseHelper<BahnModel> getParseHelper();

    protected abstract Provider<ResourceSet> getResourceSetProvider();
}

package de.uniba.swt.dsl.tests.helpers;

import com.google.inject.Inject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;

public class TestHelper {

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    public Resource parseValid(CharSequence src) throws Exception {
        Resource input = resourceHelper.resource(src);
        validationTestHelper.assertNoErrors(input);
        return input;
    }
}

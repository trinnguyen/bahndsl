package de.uniba.swt.dsl.tests.validation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.bahn.BahnPackage;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class SwtBahnFuncValidatorTest extends AbstractValidationTest {

    @Inject
    Provider<ResourceSet> resourceSetProvider;

    @Inject
    ParseHelper<BahnModel> parseHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    // @Test
    public void warningMissing2Functions() {
        var src = "def test() end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "Neither request_route nor drive_route is implemented");
    }

    // @Test
    public void warningMissingDriveRoute() {
        var src = "def request_route(string src_signal_id, string dst_signal_id, string train_id): string return \"\" end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "drive_route is not implemented");
    }

    // @Test
    public void warningMissingRequestRoute() {
        var src = "def drive_route(string route_id, string train_id) end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "request_route is not implemented");
    }

    @Override
    protected ParseHelper<BahnModel> getParseHelper() {
        return parseHelper;
    }

    @Override
    protected Provider<ResourceSet> getResourceSetProvider() {
        return resourceSetProvider;
    }
}

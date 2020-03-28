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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ConfigurationValidationTest extends AbstractValidationTest {

    @Inject
    Provider<ResourceSet> resourceSetProvider;

    @Inject
    ParseHelper<BahnModel> parseHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    @ParameterizedTest
    @ValueSource(strings = {
            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "    end\n" +
            "    signals master\n" +
            "        entry sig1 0x00\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg1\n" +
            "    end\n" +
            "    layout\n" +
            "        sig1 -- b1.down\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "    end\n" +
            "    points master\n" +
            "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- p1.stem\n" +
            "        b2.up -- p1.straight\n" +
            "        b3.down -- p1.side\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "        seg5 0x04 length 11cm\n" +
            "    end\n" +
            "    points master\n" +
            "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "        b4 main seg5\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- p1.down1\n" +
            "        b2.up -- p1.down2\n" +
            "        b3.down -- p1.up1\n" +
            "        b4.down -- p1.up2\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "        seg5 0x04 length 11cm\n" +
            "    end\n" +
            "    crossings\n" +
            "        c1 segment seg1\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "        b4 main seg5\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- c1.down1\n" +
            "        b2.up -- c1.down2\n" +
            "        b3.down -- c1.up1\n" +
            "        b4.down -- c1.up2\n" +
            "        b1.down -- b2.down\n" +
            "        b3.up -- b4.up\n" +
            "    end\n" +
            "end"
    })
    public void validLayoutTest(String src) throws Exception {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @Test
    public void errorPointConnectorTest() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "        b2 main seg1\n" +
                "    end\n" +
                "    layout\n" +
                "        p1.stem -- b1.down\n" +
                "        p1.straight -- b2.down\n" +
                "        p1.side -- b1.up\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Point must connect to 3 different blocks: p1");
    }

    @Test
    public void testErrorBlockConnector() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "        b2 main seg1\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -- p1.stem\n" +
                "        b1.up -- p1.side\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Block must connect to 2 different blocks: b1");
    }

    @Test
    public void testErrorNotConnected() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master\n" +
                "       seg1 0x01 length 11cm\n" +
                "       seg2 0x02 length 11cm\n" +
                "       seg3 0x03 length 11cm\n" +
                "    end" +
                "    signals master\n" +
                "       entry sig1 0x00\n" +
                "    end" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "        b2 main seg2\n" +
                "        b3 main seg3\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -- sig1\n" +
                "        b2.up -- b3.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Network layout is not strongly connected");
    }

    @Test
    public void testErrorNetworkBuilderDuplicatedBlockDirection() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -> b1.up\n" +
                "        b1.up -> b1.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Block direction is already defined");
    }

    @Test
    public void testErrorNetworkBuilderSignal2() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    signals master\n" +
                "        exit sig1 0x01\n" +
                "    end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    layout\n" +
                "        p1.stem -- sig1\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block");
    }

    @Test
    public void testErrorNetworkBuilderSignal1() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    signals master\n" +
                "        entry sig1 0x01\n" +
                "    end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    layout\n" +
                "        sig1 -- p1.stem\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block");
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

package de.uniba.swt.dsl.formatting2;

import com.google.inject.Inject;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnFormatterTest {

    @Inject
    Serializer serializer;

    @Inject
    TestHelper testHelper;

    @Test
    public void testFormatterExpr() throws Exception {
        var fmt = formatCode("def test() end");
        var expected = "def test()\nend\n";
        assertEquals(expected, fmt);
    }

    @ParameterizedTest
    @ValueSource (strings = {
            TestConstants.SampleLayoutConfig,
            TestConstants.SampleLayoutDoubleSlipConfig,
            TestConstants.SampleTrainConfig1,
            TestConstants.SampleTrainConfig2,
    })
    public void testFormatConfigNoError(String src) throws Exception {
        var result = formatCode(src);

        // check again
        var resource = testHelper.parseValid(result);
        var model = BahnUtil.getRootModule(resource);
        assertTrue(model.getName() != null && !model.getName().isEmpty(), "Model name must be exist");
    }

    @ParameterizedTest
    @ValueSource (strings = {
            TestConstants.SampleOperators,
            TestConstants.SampleIfElse,
            TestConstants.SampleWhile,
            TestConstants.SampleRequestRouteForeach,
            TestConstants.SampleDriveRoute
    })
    public void testFormatExpressions(String src) throws Exception {
        var result = formatCode(src);
        // verify
        var resource = testHelper.parseValid(result);
        var decl = BahnUtil.getDecls(testHelper.parseValid(result)).get(0);
        assertTrue(decl.getName() != null && !decl.getName().isEmpty(), "Model name must be exist");
    }

    @Test
    public void testFormatRequestRoute() throws Exception {
        var result = formatCode(TestConstants.SampleRequestRouteForeach);

        // verify
        var resource = testHelper.parseValid(result);
        assertEquals("request_route", BahnUtil.getDecls(resource).get(0).getName());
    }

    @Test
    public void testFormatDriveRoute() throws Exception {
        var result = formatCode(TestConstants.SampleDriveRoute);

        // verify
        var resource = testHelper.parseValid(result);
        assertEquals("drive_route", BahnUtil.getDecls(resource).get(0).getName());
    }

    @Test
    public void testFormatStandardLib() throws Exception {
        var input = testHelper.parseValid("");
        StandardLibHelper.loadStandardLibResource(input.getResourceSet());

        // find the standard lib resource
        Resource standardRes = null;
        for (Resource resource : input.getResourceSet().getResources()) {
            if (resource != input) {
                standardRes = resource;
                break;
            }
        }

        // format
        if (standardRes != null) {
            var out = formatResource(standardRes);
            assertTrue(out != null && !out.isEmpty(), "Formatted standard lib must not be empty");
        } else {
            fail("Failed to load the standard library");
        }
    }

    /**
     * BahnFormatter is called using Serializer
     * @param src
     * @return
     * @throws Exception
     */
    private String formatCode(String src) throws Exception {
        var input = testHelper.parseValid(src);
        return formatResource(input);
    }

    private String formatResource(Resource input) {
        return serializer.serialize(input.getContents().get(0), SaveOptions.newBuilder().format().getOptions());
    }
}
package de.uniba.swt.dsl.formatting2;

import com.google.inject.Inject;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnFormatterTest {
    @Inject
    BahnFormatter formatter;

    @Inject
    Serializer serializer;

    @Inject
    ResourceHelper resourceHelper;

    @Test
    public void testFormatterExpr() throws Exception {
        var fmt = formatCode("def test() end");
        var expected = "def test()\nend\n";
        assertEquals(expected, fmt);
    }

    private String formatCode(String src) throws Exception {
        var input = resourceHelper.resource(src);
        return serializer.serialize(input.getContents().get(0), SaveOptions.newBuilder().format().getOptions());
    }
}
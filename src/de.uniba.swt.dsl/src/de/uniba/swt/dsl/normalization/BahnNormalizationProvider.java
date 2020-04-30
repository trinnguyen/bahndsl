package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import org.apache.log4j.Logger;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;

import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    private final static Logger logger = Logger.getLogger(BahnNormalizationProvider.class);

    @Inject
    ArrayLookupTable arrayLookupTable;

    @Inject
    TemporaryVarGenerator varGenerator;

    @Inject
    SyntacticExprNormalizer syntacticExprNormalizer;

    @Inject
    BasicStatementNormalizer basicStatementNormalizer;

    @Inject
    StringEqualNormalizer stringEqualNormalizer;

    @Inject
    ArrayNormalizer arrayNormalizer;

    @Inject
    ForeachNormalizer foreachNormalizer;

    @Inject
    Serializer serializer;

    public BahnNormalizationProvider() {
    }

    public void normalize(List<FuncDecl> decls) {
        if (decls == null || decls.size() == 0)
            return;

        for (FuncDecl decl : decls) {
            // reset
            beforeNormalize(decl);

            // normalize
            normalizeFunc(decl);
        }
    }

    private void beforeNormalize(FuncDecl decl) {
        varGenerator.resetFunc(decl.getName());
        arrayLookupTable.resetFunc(decl.getName());
    }

    /**
     * Normalize function with several providers
     * Order is important
     * @param funcDecl
     */
    private void normalizeFunc(FuncDecl funcDecl) {
        // break multiple operator expression into small (basic) statement
        basicStatementNormalizer.normalizeFunc(funcDecl);

        // convert list to array with additional length variable
        arrayNormalizer.normalizeFunc(funcDecl);

        // convert syntactic sugar foreach to while iteration
        foreachNormalizer.normalizeFunc(funcDecl);

        // convert string comparision expression using extern C function
        stringEqualNormalizer.normalizeFunc(funcDecl);

        // convert all getter/setter for configuration and track state
        syntacticExprNormalizer.normalizeFunc(funcDecl);
    }

    private void log(FuncDecl funcDecl) {
        if (funcDecl.getName().equals("test")) {
            var input = funcDecl.eResource().getContents().get(0);
            logger.debug(serializer.serialize(input, SaveOptions.newBuilder().format().getOptions()));
        }
    }
}

package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;

@Singleton
public class BahnNormalizationProvider {

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

    public BahnNormalizationProvider() {
    }

    public void normalize(RootModule rootModule) {

        for (ModuleProperty property : rootModule.getProperties()) {
            if (property instanceof FuncDecl) {

                // reset
                varGenerator.resetFunc(((FuncDecl) property).getName());
                arrayLookupTable.resetFunc(((FuncDecl) property).getName());

                // normalize
                normalizeFunc(((FuncDecl) property));
            }
        }
    }

    private void normalizeFunc(FuncDecl funcDecl) {
        // convert list to array with additional length variable
        arrayNormalizer.normalizeFunc(funcDecl);

        // convert syntactic sugar foreach to while iteration
        foreachNormalizer.normalizeFunc(funcDecl);

        // convert string comparision expression using extern C function
        stringEqualNormalizer.normalizeFunc(funcDecl);

        // convert all getter/setter for configuration and track state
        syntacticExprNormalizer.normalizeFunc(funcDecl);

        // break multiple operator expression into small (basic) statement
        basicStatementNormalizer.normalizeFunc(funcDecl);
    }
}

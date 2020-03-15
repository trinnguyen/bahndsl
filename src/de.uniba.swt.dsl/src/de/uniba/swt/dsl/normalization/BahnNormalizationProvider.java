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
    SyntacticExprNormalizer expressionNormalizer;

    @Inject
    StatementNormalizer statementNormalizer;

    @Inject
    StringEqualNormalizer stringEqualNormalizer;

    @Inject
    ArrayNormalizer arrayNormalizer;

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
        arrayNormalizer.normalizeFunc(funcDecl);
        stringEqualNormalizer.normalizeFunc(funcDecl);
        expressionNormalizer.normalizeFunc(funcDecl);
        statementNormalizer.normalizeFunc(funcDecl);
    }
}

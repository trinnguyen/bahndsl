package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;

@Singleton
public class BahnNormalizationProvider {

    @Inject
    TemporaryVarGenerator varGenerator;

    @Inject
    SyntacticExprNormalizer expressionNormalizer;

    @Inject
    StatementNormalizer statementNormalizer;

    @Inject
    StringEqualNormalizer stringEqualNormalizer;

    public BahnNormalizationProvider() {
    }

    public void normalize(RootModule rootModule) {
        varGenerator.reset();

        for (ModuleProperty property : rootModule.getProperties()) {
            if (property instanceof FuncDecl) {
                normalizeFunc(((FuncDecl) property));
            }
        }
    }

    private void normalizeFunc(FuncDecl funcDecl) {
        varGenerator.setFunctionName(funcDecl.getName());

        stringEqualNormalizer.normalizeFunc(funcDecl);
        expressionNormalizer.normalizeFunc(funcDecl);
        statementNormalizer.normalizeFunc(funcDecl);
    }
}

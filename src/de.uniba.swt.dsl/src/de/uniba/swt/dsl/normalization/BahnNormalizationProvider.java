package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.StringUtil;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collection;
import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    @Inject
    TemporaryVarGenerator varGenerator;

    @Inject
    ExpressionNormalizer expressionNormalizer;

    @Inject
    StatementNormalizer statementNormalizer;

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
        expressionNormalizer.normalizeFunc(funcDecl);
        statementNormalizer.normalizeFunc(funcDecl);
    }
}

package de.uniba.swt.expr.generator;

import java.util.Objects;

import org.eclipse.emf.common.util.EList;

import com.google.inject.Singleton;

import de.uniba.swt.expr.bahnexpr.FuncDecl;

@Singleton
public class SCChartsGenerator {
    public String generateModel(EList<FuncDecl> decls) {
        FuncDecl mainDecl = decls.stream().filter(f -> Objects.equals(f.getName().toLowerCase(), "main")).findFirst().orElse(null);
        if (mainDecl != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("scchart bahnexpr {\n");
            builder.append("input string srcSignalId, destSignalId, trainId\n");
            builder.append("output string routeId\n");
            builder.append("}");
            return builder.toString();
        }

        return null;
    }
}

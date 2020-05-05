/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

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
        arrayLookupTable.resetFunc();
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

        log(funcDecl);
    }

    private void log(FuncDecl funcDecl) {
        if (funcDecl.getName().equals("drive_route")) {
            var input = funcDecl.eResource().getContents().get(0);
            logger.debug(serializer.serialize(input, SaveOptions.newBuilder().format().getOptions()));
        }
    }
}

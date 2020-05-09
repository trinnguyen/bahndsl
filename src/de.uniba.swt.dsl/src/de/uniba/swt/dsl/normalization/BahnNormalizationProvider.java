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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;

import java.util.ArrayList;
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
    VariableNameNormalizer variableNameNormalizer;

    @Inject
    Serializer serializer;

    private List<AbstractNormalizer> normalizers;

    public void setNormalizers(List<AbstractNormalizer> normalizers) {
        this.normalizers = normalizers;
    }

    public void normalize(List<FuncDecl> decls) {
        prepare();
        if (decls == null || decls.size() == 0)
            return;

        for (FuncDecl decl : decls) {
            // reset
            beforeNormalize(decl);

            // normalize
            normalizeFunc(decl);
        }

        log(decls.get(0));
    }

    private void prepare() {
        if (normalizers == null) {
            normalizers = new ArrayList<>();

            // break multiple operator expression into small (basic) statement
            normalizers.add(basicStatementNormalizer);

            // convert list to array with additional length variable
            normalizers.add(arrayNormalizer);

            // convert syntactic sugar foreach to while iteration
            normalizers.add(foreachNormalizer);

            // convert string comparision expression using extern C function
            normalizers.add(stringEqualNormalizer);

            // convert all getter/setter for configuration and track state
            normalizers.add(syntacticExprNormalizer);

            // rename before sending to sccharts model
            normalizers.add(variableNameNormalizer);
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
        for (AbstractNormalizer normalizer : normalizers) {
            normalizer.normalizeFunc(funcDecl);
        }
    }

    private void log(FuncDecl funcDecl) {
        for (Resource resource : funcDecl.eResource().getResourceSet().getResources()) {
            if (resource.getContents().size() > 0) {
                logger.debug(serializer.serialize(resource.getContents().get(0), SaveOptions.newBuilder().format().getOptions()));
            }
        }
    }
}

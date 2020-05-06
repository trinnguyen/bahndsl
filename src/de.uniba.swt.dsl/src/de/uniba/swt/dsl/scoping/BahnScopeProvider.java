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

/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.scoping;


import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import de.uniba.swt.dsl.bahn.*;

import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.FilteringScope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
public class BahnScopeProvider extends AbstractBahnScopeProvider {
    private static final Logger logger = LogManager.getLogger(BahnScopeProvider.class);

    @Override
    public IScope getScope(EObject context, EReference reference) {

        // limit properties for a type from schema
        if (context instanceof GetConfigFuncExpr && reference == BahnPackage.Literals.GET_CONFIG_FUNC_EXPR__PROP) {
            GetConfigFuncExpr expr = (GetConfigFuncExpr) context;
            return Scopes.scopeFor(expr.getType().getProperties());
        }

        // set config allow the route and train only
        if (context instanceof SetConfigFuncExpr) {

            // set config for route only
            if (reference == BahnPackage.Literals.SET_CONFIG_FUNC_EXPR__TYPE) {
                var items = super.getScope(context, reference);
                return new FilteringScope(items, input -> {
                    if (input != null && input.getEObjectOrProxy() instanceof SchemaElement) {
                        return BahnConstants.SET_CONFIG_ROUTE_TYPE.equalsIgnoreCase(((SchemaElement)input.getEObjectOrProxy()).getName());
                    }

                    return false;
                });
            }

            // set config for train_id only
            if (reference == BahnPackage.Literals.SET_CONFIG_FUNC_EXPR__PROP) {
                SetConfigFuncExpr expr = (SetConfigFuncExpr) context;
                return Scopes.scopeFor(expr.getType().getProperties().stream().filter(p -> p.getName().equalsIgnoreCase(BahnConstants.SET_CONFIG_TRAIN_NAME)).collect(Collectors.toList()));
            }
        }

        // load custom class
        var instanceClass = reference.getEReferenceType().getInstanceClass();
        var candidates = getGlobalCandidates(context, instanceClass);
        if (candidates != null) {
            var scope = Scopes.scopeFor(candidates);

            // filter for each type of config key
            if (instanceClass.equals(ConfigKey.class)) {
                return filterScopeForConfigKey(scope, reference);
            }

            return scope;
        }

        // default
        return super.getScope(context, reference);
    }

    private IScope filterScopeForConfigKey(IScope scope, EReference reference) {
        return new FilteringScope(scope, input -> {
            List<String> names = null;
            if (reference == BahnPackage.Literals.SEGMENT_ELEMENT__KEY_LENGTH) {
                names = List.of("length");
            } else if (reference == BahnPackage.Literals.POINT_ELEMENT__KEY) {
                names = List.of("segment");
            } else if (reference == BahnPackage.Literals.CROSSING_ELEMENT__KEY) {
                names = List.of("segment");
            } else if (reference == BahnPackage.Literals.BLOCK_ELEMENT__LIMIT_KEY) {
                names = List.of("limit");
            } else if (reference == BahnPackage.Literals.CONFIG_PROP__KEY) {
                names = List.of("type", "weight", "length");
            }

            if (names != null)
                return input != null && input.getName() != null && names.contains(input.getName().toString());

            return true;
        });
    }

    private List<? extends EObject> getGlobalCandidates(EObject context, Class<?> instanceClass) {

        List<? extends EObject> candidates;

        // config key
        if (instanceClass.equals(ConfigKey.class)) {
            candidates = getCandidates(context, ConfigKey.class);
            return candidates;
        }

        // signal type
        if (instanceClass.equals(SignalType.class)) {
            candidates = getCandidates(context, SignalType.class);
            return candidates;
        }

        // fundecl
        if (instanceClass.equals(FuncDecl.class)) {
            candidates = getCandidates(context, FuncDecl.class);
            return candidates;
        }

        // SchemaElement
        if (instanceClass.equals(SchemaElement.class)) {
            candidates = getCandidates(context, SchemaElement.class);
            return candidates;
        }

        return null;
    }

    private <T extends EObject> List<T> getCandidates(EObject context, Class<T> type) {
        return Lists.newArrayList(Iterators.filter(EcoreUtil2.getAllContents(context.eResource().getResourceSet(), false), type));
    }
}

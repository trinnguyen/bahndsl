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

package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.*;
import java.util.function.Function;

public class BoardValidator {
    private final List<Class> supportedSingleSectionClasses = List.of(
            BoardsProperty.class,
            BlocksProperty.class,
            PlatformsProperty.class,
            CrossingsProperty.class,
            LayoutProperty.class,
            TrainsProperty.class);

    public List<Tuple<String, Integer>> findSingleSectionError(RootModule module) {
        List<Tuple<String, Integer>> items = new ArrayList<>();

        Set<String> flags = new HashSet<>();
        for (int i = 0; i < module.getProperties().size(); i++) {
            var prop = module.getProperties().get(i);
            var supportedClass = supportedSingleSectionClasses.stream().filter(c -> c.isInstance(prop)).findFirst();
            if (supportedClass.isEmpty())
                continue;

            // add up
            var name = supportedClass.get().getSimpleName().replace("Property", "").toLowerCase();
            if (!flags.contains(name)) {
                flags.add(name);
            } else {
                items.add(Tuple.of(String.format(ValidationErrors.SingleSectionFormat, name), i));
            }
        }

        return items;
    }

    public List<Tuple<String, Integer>> findSingleTrackByBoardErrors(RootModule module) {
        List<Tuple<String, Integer>> errors = new ArrayList<>();

        Set<Tuple<String, String>> flags = new HashSet<>();
        for (int i = 0; i < module.getProperties().size(); i++) {
            var prop = module.getProperties().get(i);
            Tuple<String, String> sectionBoardPair = getSectionInfo(prop);
            if (sectionBoardPair == null)
                continue;

            // add up
            if (!flags.contains(sectionBoardPair)) {
                flags.add(sectionBoardPair);
            } else {
                var boardName = sectionBoardPair.getFirst();
                var sectionName = sectionBoardPair.getSecond();
                errors.add(Tuple.of(String.format(ValidationErrors.SingleSectionByBoardFormat, sectionName, boardName), i));
            }
        }

        return errors;
    }

    private Tuple<String, String> getSectionInfo(ModuleProperty prop) {
        if (prop instanceof SegmentsProperty) {
            return Tuple.of(((SegmentsProperty) prop).getBoard().getName(), "segments");
        }

        if (prop instanceof SignalsProperty) {
            return Tuple.of(((SignalsProperty) prop).getBoard().getName(), "signals");
        }

        if (prop instanceof PeripheralsProperty) {
            return Tuple.of(((PeripheralsProperty) prop).getBoard().getName(), "peripherals");
        }

        if (prop instanceof PointsProperty) {
            return Tuple.of(((PointsProperty) prop).getBoard().getName(), "points");
        }

        return null;
    }

    public <T> List<Tuple<String, Integer>> validateUniqueHex(List<T> items, Function<T, String> addrMapper) {
        List<Tuple<String, Integer>> result = new ArrayList<>();
        Set<Long> values = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            var hexVal = addrMapper.apply(items.get(i));
            Long hexAddr = BahnUtil.parseHex(hexVal);

            if (values.contains(hexAddr)) {
                result.add(Tuple.of(String.format(ValidationErrors.DefinedBoardAddressFormat, hexVal), i));
            } else {
                values.add(hexAddr);
            }
        }
        return result;
    }
}

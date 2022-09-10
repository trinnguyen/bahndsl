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

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.*;
import java.util.function.Function;

public class UniqueReverserValidator {
    // Track output addresses and Accessory addresses have separate address spaces.
    private final Map<String, Set<Integer>> mapBoardReverserCv = new HashMap<>();

    private final Set<BlockElement> mapBoardReverserBlock = new HashSet<>();

    public <T> List<Tuple<String, Integer>> validateUniqueReverser(String boardName, List<T> items, Function<T, Integer> cvMapper, Function<T, BlockElement> blockMapper) {
        List<Tuple<String, Integer>> errors = new ArrayList<>();

        if (!mapBoardReverserCv.containsKey(boardName)) {
            mapBoardReverserCv.put(boardName, new HashSet<>());
        }

        var setCv = mapBoardReverserCv.get(boardName);

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);

            Integer cv = cvMapper.apply(item);
            BlockElement block = blockMapper.apply(item);

            // verify duplication
            if (setCv.contains(cv)) {
                errors.add(Tuple.of(String.format(ValidationErrors.DefinedReverserCvFormat, cv, boardName), i));
            } else {
                setCv.add(cv);
            }

            if (mapBoardReverserBlock.contains(block)) {
                errors.add(Tuple.of(String.format(ValidationErrors.DefinedReverserBlockFormat, block.getName(), boardName), i));
            } else {
                mapBoardReverserBlock.add(block);
            }
        }

        return errors;
    }

    public void clear() {
        mapBoardReverserCv.clear();
        mapBoardReverserBlock.clear();
    }
}

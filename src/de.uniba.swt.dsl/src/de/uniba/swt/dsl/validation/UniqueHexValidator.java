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

import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.*;
import java.util.function.Function;

public class UniqueHexValidator {
    private final Map<String, Set<Long>> mapBoardHex = new HashMap<>();

    public <T> List<Tuple<String, Integer>> validateUniqueAddress(String boardName, List<T> items, Function<T, String> addrMapper) {
        List<Tuple<String, Integer>> errors = new ArrayList<>();
        if (!mapBoardHex.containsKey(boardName)) {
            mapBoardHex.put(boardName, new HashSet<>());
        }

        var set = mapBoardHex.get(boardName);

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);

            // verify hex value
            String strVal = addrMapper.apply(item);
            if (strVal == null || strVal.isBlank())
                continue;

            Long val = null;
            try {
                val = BahnUtil.parseHex(strVal);
            } catch (NumberFormatException e) {
                errors.add(Tuple.of(String.format(ValidationErrors.InvalidHexFormat, strVal), i));
            }

            if (val == null)
                continue;

            // verify duplication
            if (set.contains(val)) {
                errors.add(Tuple.of(String.format(ValidationErrors.DefinedAddressFormat, strVal, boardName), i));
            } else {
                set.add(val);
            }
        }

        return errors;
    }

    public void clear() {
        mapBoardHex.clear();
    }
}

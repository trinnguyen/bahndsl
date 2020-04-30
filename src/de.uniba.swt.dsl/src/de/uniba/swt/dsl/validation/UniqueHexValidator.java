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

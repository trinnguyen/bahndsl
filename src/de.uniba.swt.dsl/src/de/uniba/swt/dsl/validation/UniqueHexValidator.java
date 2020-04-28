package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.common.util.BahnUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class UniqueHexValidator {
    public <T> void validateUniqueAddress(List<T> items, Function<T, String> addrMapper) throws Exception {
        Set<Long> set = new HashSet<>();
        for (T item : items) {
            String strVal = addrMapper.apply(item);
            Long val = BahnUtil.parseHex(strVal);
            if (set.contains(val)) {
                throw new Exception("Address is already used in another element: " + strVal);
            }

            set.add(val);
        }
    }
}

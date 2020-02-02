package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.common.models.Util;
import org.eclipse.emf.common.util.EList;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UniqueHexValidator {
    public <T> void validateUniqueAddress(EList<T> items, Function<T, String> addrMapper) throws Exception {
        Set<Long> set = new HashSet<>();
        for (T item : items) {
            String strVal = addrMapper.apply(item);
            Long val = Util.parseHex(strVal);
            if (set.contains(val)) {
                throw new Exception("Address is already used in another element: " + strVal);
            }

            set.add(val);
        }
    }
}

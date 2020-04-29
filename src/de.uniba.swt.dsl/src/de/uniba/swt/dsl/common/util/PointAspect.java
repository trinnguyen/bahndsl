package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.PointAspectType;

import java.util.Objects;

public class PointAspect {
    private final PointAspectType aspectType;
    private final String hexValue;

    public PointAspect(PointAspectType aspectType, String hexValue) {
        this.aspectType = aspectType;
        this.hexValue = hexValue;
    }

    public PointAspectType getAspectType() {
        return aspectType;
    }

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointAspect)) return false;

        PointAspect that = (PointAspect) o;

        if (aspectType != that.aspectType) return false;
        return Objects.equals(hexValue, that.hexValue);
    }

    @Override
    public int hashCode() {
        int result = aspectType != null ? aspectType.hashCode() : 0;
        result = 31 * result + (hexValue != null ? hexValue.hashCode() : 0);
        return result;
    }
}

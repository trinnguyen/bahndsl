package de.uniba.swt.dsl.common.util;

import java.util.Set;

public class BahnConstants {
    public static final String ENTRY_FUNC_NAME = "interlocking";

    public static final String SWITCH_STRAIGHT = "straight";

    public static final String SWITCH_SIDE = "side";

    public static final String CROSSING_UP_1 = "up1";
    public static final String CROSSING_UP_2 = "up2";
    public static final String CROSSING_DOWN_1 = "down1";
    public static final String CROSSING_DOWN_2 = "down2";

    public static final String CONNECTOR_UNDIRECTED = "--";
    public static final String CONNECTOR_DIRECTED = "->";

    public static final Set<String> BLOCK_PROPS;

    public static final Set<String> SWITCH_PROPS;

    public static final Set<String> CROSSING_PROPS;

    static {
        BLOCK_PROPS = Set.of("up", "down");
        SWITCH_PROPS = Set.of("stem", SWITCH_STRAIGHT, SWITCH_SIDE);
        CROSSING_PROPS = Set.of(CROSSING_UP_1, CROSSING_UP_2, CROSSING_DOWN_1, CROSSING_DOWN_2);
    }
}

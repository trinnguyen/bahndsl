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

package de.uniba.swt.dsl.common.util;

import java.util.Set;

public class BahnConstants {

    public static final String STANDARD_SWITCH_STRAIGHT = "straight";

    public static final String STANDARD_SWITCH_SIDE = "side";

    public static final String DOUBLE_SLIP_SWITCH_UP_1 = "up1";

    public static final String DOUBLE_SLIP_SWITCH_UP_2 = "up2";

    public static final String DOUBLE_SLIP_SWITCH_DOWN_1 = "down1";

    public static final String DOUBLE_SLIP_SWITCH_DOWN_2 = "down2";

    public static final String CROSSING_UP_1 = "up1";

    public static final String CROSSING_UP_2 = "up2";

    public static final String CROSSING_DOWN_1 = "down1";

    public static final String CROSSING_DOWN_2 = "down2";

    public static final String CONNECTOR_UNDIRECTED = "--";

    public static final String CONNECTOR_DIRECTED = "->";

    public static final Set<String> BLOCK_PROPS;

    public static final Set<String> STANDARD_SWITCH_PROPS;

    public static final Set<String> DOUBLE_SLIP_SWITCH_PROPS;

    public static final Set<String> CROSSING_PROPS;

    public static final String REQUEST_ROUTE_SCTX = "request_route_sccharts.sctx";

    public static final String DRIVE_ROUTE_SCTX = "drive_route_sccharts.sctx";

    public static final String REQUEST_ROUTE_FUNC_NAME = "request_route";

    public static final String DRIVE_ROUTE_FUNC_NAME = "drive_route";

    public static final String SET_CONFIG_TRAIN_NAME = "train";

    public static final String SET_CONFIG_ROUTE_TYPE = "route";

    public static final int DEFAULT_ARRAY_SIZE = 1024;

    static {
        BLOCK_PROPS = Set.of("up", "down");
        STANDARD_SWITCH_PROPS = Set.of("stem", STANDARD_SWITCH_STRAIGHT, STANDARD_SWITCH_SIDE);
        DOUBLE_SLIP_SWITCH_PROPS = Set.of(DOUBLE_SLIP_SWITCH_UP_1, DOUBLE_SLIP_SWITCH_UP_2, DOUBLE_SLIP_SWITCH_DOWN_1, DOUBLE_SLIP_SWITCH_DOWN_2);
        CROSSING_PROPS = Set.of(CROSSING_UP_1, CROSSING_UP_2, CROSSING_DOWN_1, CROSSING_DOWN_2);
    }
}

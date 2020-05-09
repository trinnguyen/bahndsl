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

import java.util.Locale;

public class ValidationErrors {

    /**
     * Errors for configuration
     */

    public final static String DefinedAddressFormat = "Address '%s' is already used in the board '%s'";

    public final static String InvalidHexFormat = "Hex number '%s' is invalid";

    public final static String DefinedConfigNameFormat = "Name '%s' is already defined";

    public final static String DefinedBoardAddressFormat = "Address '%s' is already used by another board";

    public final static String SingleSectionFormat = "'%s' section is already defined";

    public final static String SingleSectionByBoardFormat = "'%s' section is already defined for board '%s'";

    public final static String UsedSegmentFormat = "Segment '%s' is already used by '%s'";

    public final static String UsedSegmentInMainFormat = "Segment '%s' is already used as main segment";

    public final static String UsedSegmentInOverlapFormat = "Segment '%s' is already used as overlap segment";

    public final static String DirectionBlockOnly = "Direction is allowed for block only";

    public final static String DirectionSameBlock = "Direction must be configured on the same block";

    public final static String InvalidConnectorsFormat = "Invalid layout connector, use: '%s'";

    public final static String SignalConnectorNoAllowed = "Connector is not supported in signal";

    public final static String NotSupportedConnectorElementFormat = "Connector is allowed for signal, point, block, platform, crossing, unexpected '%s'";

    public final static String MissingConnector = "Missing connector property";

    public final static String MissingLayoutEndpointFormat = "%s must connect to %d different endpoints";

    public final static String NetworkNotValid = "Network layout is not strongly connected";

    public final static String SingleToBlockOnly = "Signal can only connect to a block";

    public final static String DefinedBlockDirectionFormat = "Direction of '%s' is already defined" ;

    public final static String UsedSignalInCompositionFormat = "'%s' is already used";

    public final static String UsedSignalTypeInCompositionFormat = "Type '%s' is already used";

    /**
     * Errors for expression
     */

    public final static String IdUnderscoreNotAllowedBeginning = "Underscore is not allowed at the beginning";

    public final static String MissingArrayAssignment = "Missing assignment to an array variable";

    public final static String TypeErrorFormat = "Type Error: Expected '%s' but found '%s'";

    public final static String TypeExpectedArray = "Type Error: Expected array";

    public final static String ExpectedSameExpressionType = "Type Error: Expected same type but found '%s' and '%s'";

    public final static String WrongArgumentsSizeFormat = "Expected %d arguments but found %d";

    public final static String MissingReturn = "Missing return statement";

    public final static String UnexpectedReturn = "Unexpected return statement";

    public final static String BreakInIteration = "break can only be used inside 'for..in' or 'while'";

    public final static String DefinedVariableFormat = "Variable '%s' is already defined";

    public final static String DefinedFuncFormat = "Function '%s' is already defined";

    public final static String ReadonlyParameterFormat = "'%s' is readonly parameter";

    private ValidationErrors() {}

    public static String createTypeErrorMsg(String expectedType, String actualType) {
        return String.format(TypeErrorFormat, expectedType, actualType);
    }
}

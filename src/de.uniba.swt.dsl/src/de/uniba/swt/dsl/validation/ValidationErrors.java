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

    public final static String SingleSectionByBoardFormat = "'%s' is already defined for board '%s'";

    public final static String UsedSegmentFormat = "Segment '%s' is already used by '%s'";

    public final static String UsedSegmentInMainFormat = "Segment '%s' is already used as main segment";

    public final static String UsedSegmentInOverlapFormat = "Segment '%s' is already used as overlap segment";

    public final static String DirectionBlockOnly = "Direction is allowed for block only";

    public final static String DirectionSameBlock = "Direction must be configured on the same block";

    public final static String InvalidConnectorsFormat = "Invalid layout connector, use: '%s'";

    public final static String SinglaConnectorNoAllowed = "Connector is not supported in signal";

    public final static String NotSupportedConnectorElementFormat = "Connector is allowed for signal, point, block, platform, crossing, unexpected '%s'";

    public final static String MissingConnector = "Missing connector property";

    public final static String MissingLayoutEndpointFormat = "%s must connect to %d different endpoints";

    public final static String NetworkNotValid = "Network layout is not strongly connected";

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

    public final static String BreakInIteration = "break can only be used inside 'for..in";

    private ValidationErrors() {}
}

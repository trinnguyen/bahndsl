package de.uniba.swt.expr.converter;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

import com.google.inject.Inject;

public class BahnExprValueConverterService extends DefaultTerminalConverters {
    @Inject
    HEXValueConverter hexValueConveter;

    @ValueConverter(rule = "HEX")
    public IValueConverter<Long> HEX() {
        return hexValueConveter;
    }
}

package de.uniba.swt.dsl.converter;

import com.google.inject.Inject;
import de.uniba.swt.expr.converter.HEXValueConverter;
import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

public class BahnValueConverterService extends DefaultTerminalConverters {
    @Inject
    HEXValueConverter hexValueConveter;

    @ValueConverter(rule = "HEX")
    public IValueConverter<Long> HEX() {
        return hexValueConveter;
    }
}

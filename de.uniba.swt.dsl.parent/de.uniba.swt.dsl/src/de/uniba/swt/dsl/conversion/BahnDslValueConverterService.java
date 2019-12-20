package de.uniba.swt.dsl.conversion;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BahnDslValueConverterService extends DefaultTerminalConverters {
	@Inject
	HEXValueConverter hexValueConveter;
	
	@ValueConverter(rule = "HEX")
	public IValueConverter<Long> HEX() {
		return hexValueConveter;
	}
}

package de.uniba.swt.dsl.conversion

import org.eclipse.xtext.common.services.DefaultTerminalConverters
import org.eclipse.xtext.conversion.ValueConverter
import com.google.inject.Inject
import org.eclipse.xtext.conversion.IValueConverter
import com.google.inject.Singleton

@Singleton
class BahnDslValueConverterService extends DefaultTerminalConverters {
	
	@Inject
	HEXValueConverter hexValueConveter;
	
	@ValueConverter(rule = "HEX")
	def IValueConverter<Long> HEX() {
		return hexValueConveter;
	}
}
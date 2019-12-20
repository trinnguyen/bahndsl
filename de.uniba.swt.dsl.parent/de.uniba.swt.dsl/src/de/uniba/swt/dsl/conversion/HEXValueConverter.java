package de.uniba.swt.dsl.conversion;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Strings;

public class HEXValueConverter extends AbstractLexerBasedConverter<Long> {
	
	@Override
	protected void assertValidValue(Long value) {
		super.assertValidValue(value);
		if (value < 0)
			throw new ValueConverterException(getRuleName() + "-value may not be negative (value: " + value + ").", null, null);
	}

	@Override
	public Long toValue(String string, INode node) throws ValueConverterException {
		if (Strings.isEmpty(string))
			throw new ValueConverterException("Couldn't convert empty hex string.", node, null);
		
		if (!string.toLowerCase().startsWith("0x"))
			throw new ValueConverterException("Hex string must start with 0x", node, null);
		
		try {
			return Long.parseLong(string.substring(2), 16);
		} catch (Exception ex) {
			throw new ValueConverterException("Failed to parse Long from hex string", node, null);
		}
	}

}

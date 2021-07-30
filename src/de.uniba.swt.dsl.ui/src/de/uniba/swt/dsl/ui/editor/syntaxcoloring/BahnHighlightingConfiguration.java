package de.uniba.swt.dsl.ui.editor.syntaxcoloring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class BahnHighlightingConfiguration extends DefaultHighlightingConfiguration {

	public static final String STANDARDLIB_CONFIG_ID = "standardlib-config";
	
	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		
		acceptor.acceptDefaultHighlighting(STANDARDLIB_CONFIG_ID, "Standardlib configuration query", standardlibConfigTextStyle());
	}

	private TextStyle standardlibConfigTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(42, 0, 255));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}
	
}

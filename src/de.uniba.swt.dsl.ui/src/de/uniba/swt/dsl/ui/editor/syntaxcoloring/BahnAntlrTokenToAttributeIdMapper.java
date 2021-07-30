package de.uniba.swt.dsl.ui.editor.syntaxcoloring;


import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ide.editor.syntaxcoloring.HighlightingStyles;

public class BahnAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper {
	

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if ("RULE_ID".equals(tokenName)) {
			return HighlightingStyles.DEFAULT_ID;
		}
		
		if ("RULE_HEX".equals(tokenName) || "RULE_INT".equals(tokenName)) {
			return HighlightingStyles.NUMBER_ID;
		}
		
		if ("RULE_BOOLEAN".equals(tokenName)) {
			return HighlightingStyles.KEYWORD_ID;
		}
		
		if ("RULE_STRING".equals(tokenName)) {
			return HighlightingStyles.STRING_ID;
		}
		
		if ("RULE_SL_COMMENT".equals(tokenName) || "RULE_ML_COMMENT".equals(tokenName)) {
			return HighlightingStyles.COMMENT_ID;
		}
		
		return super.calculateId(tokenName, tokenType);
	}

}
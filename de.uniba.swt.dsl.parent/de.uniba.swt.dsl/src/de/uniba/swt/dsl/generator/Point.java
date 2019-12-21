package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
class Point extends Element {
	private String id;
	private String boardId;
	private long number;
	private List<Aspect> aspects;
	private Aspect initialAspect;
	
	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append(Util.SINGLE_INDENT).append("number: " + hexString(number)).append(Util.LINE_BREAK);
		
		if (aspects != null && aspects.size() > 0) {
			builder.append(indent).append(Util.SINGLE_INDENT).append("aspects:").append(Util.LINE_BREAK);
			for (Aspect aspect : aspects) {
				builder.append(aspect.dumpYaml(Util.increaseIndent(Util.increaseIndent(indent))));
			}
		}
		
		if (initialAspect != null) {
			builder.append(indent).append(Util.SINGLE_INDENT).append("initial: " + hexString(initialAspect.getValue())).append(Util.LINE_BREAK);
		}
		
		return builder.toString();
	}
}
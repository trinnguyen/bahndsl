package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
class Aspect extends Element {
	private String id;
	private long value;
	
	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append(Util.SINGLE_INDENT).append("value: " + hexString(value)).append(Util.LINE_BREAK);
		return builder.toString();
	}
}

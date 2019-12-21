package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
class TrainPeripheral extends Element {
	private String id;
	private int bit;
	private int initial;
	
	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		
		String fintent = Util.increaseIndent(indent);
		builder.append(fintent).append("bit: " + bit).append(Util.LINE_BREAK);
		builder.append(fintent).append("initial: " + initial).append(Util.LINE_BREAK);
		return builder.toString();
	}
}
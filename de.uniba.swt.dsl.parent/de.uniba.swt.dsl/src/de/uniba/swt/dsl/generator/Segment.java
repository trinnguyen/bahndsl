package de.uniba.swt.dsl.generator;

import lombok.val;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
class Segment extends Element {
	private String id;
	private String boardId;
	private long address;
	
	@Override
	public String dumpYaml(String indent) {
		val builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append("  address: " + hexString(address)).append(Util.LINE_BREAK);
		return builder.toString();
	}
}
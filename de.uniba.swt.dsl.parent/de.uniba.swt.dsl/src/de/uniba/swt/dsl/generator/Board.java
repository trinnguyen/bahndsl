package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
class Board extends Element {
	private String id;
	private long uniqueId;
	private List<BoardFeature> features;
	
	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		String findent = Util.increaseIndent(indent);
		builder.append(findent).append("unique-id: " + hexString(uniqueId)).append(Util.LINE_BREAK);
		if (features != null && features.size() > 0) {
			builder.append(findent).append("features:").append(Util.LINE_BREAK);
			findent = Util.increaseIndent(findent);
			for (BoardFeature f: features) {
				builder.append(findent).append("- number: " + hexString(f.getNumber())).append(Util.LINE_BREAK);
				builder.append(findent).append("  value: " + hexString(f.getValue())).append(Util.LINE_BREAK);
			}
		}
		return builder.toString();
	}
}
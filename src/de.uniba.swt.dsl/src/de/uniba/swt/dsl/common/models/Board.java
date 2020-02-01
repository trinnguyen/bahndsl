package de.uniba.swt.dsl.common.models;

import java.util.List;

public class Board extends Element {
	private String id;
	private String uniqueId;
	private List<BoardFeature> features;

	public Board(String id, String uniqueId, List<BoardFeature> features) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.features = features;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		String findent = Util.increaseIndent(indent);
		builder.append(findent).append("unique-id: " + uniqueId).append(Util.LINE_BREAK);
		if (features != null && features.size() > 0) {
			builder.append(findent).append("features:").append(Util.LINE_BREAK);
			findent = Util.increaseIndent(findent);
			for (BoardFeature f: features) {
				builder.append(findent).append("- number: " + f.getNumber()).append(Util.LINE_BREAK);
				builder.append(findent).append("  value: " + f.getValue()).append(Util.LINE_BREAK);
			}
		}
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public List<BoardFeature> getFeatures() {
		return features;
	}

	public void setFeatures(List<BoardFeature> features) {
		this.features = features;
	}
}
package de.uniba.swt.dsl.common.models;

public class Aspect extends Element {
	private String id;
	private String value;

	public Aspect(String id, String value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: ").append(id).append(Util.LINE_BREAK);
		builder.append(indent).append(Util.SINGLE_INDENT).append("value: " + value).append(Util.LINE_BREAK);
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

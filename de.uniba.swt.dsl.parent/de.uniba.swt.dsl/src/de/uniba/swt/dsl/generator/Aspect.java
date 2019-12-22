package de.uniba.swt.dsl.generator;

class Aspect extends Element {
	private String id;
	private long value;

	public Aspect(String id, long value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append(Util.SINGLE_INDENT).append("value: " + hexString(value)).append(Util.LINE_BREAK);
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}

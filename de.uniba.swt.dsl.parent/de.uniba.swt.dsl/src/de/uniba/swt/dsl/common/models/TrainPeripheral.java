package de.uniba.swt.dsl.common.models;

public class TrainPeripheral extends Element {
	private String id;
	private int bit;
	private int initial;

	public TrainPeripheral(String id, int bit, int initial) {
		this.id = id;
		this.bit = bit;
		this.initial = initial;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		
		String fintent = Util.increaseIndent(indent);
		builder.append(fintent).append("bit: " + bit).append(Util.LINE_BREAK);
		builder.append(fintent).append("initial: " + initial).append(Util.LINE_BREAK);
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getBit() {
		return bit;
	}

	public void setBit(int bit) {
		this.bit = bit;
	}

	public int getInitial() {
		return initial;
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}
}
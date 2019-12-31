package de.uniba.swt.dsl.common.models;

public class BoardFeature {
	private long number;
	private long value;

	public BoardFeature(long number, long value) {
		this.number = number;
		this.value = value;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
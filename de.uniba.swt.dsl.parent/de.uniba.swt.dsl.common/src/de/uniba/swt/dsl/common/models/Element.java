package de.uniba.swt.dsl.common.models;

public abstract class Element {
	
	public String hexString(long value) {
		return Util.toHexString(value);
	}
	
	public abstract String dumpYaml(String indent);
}

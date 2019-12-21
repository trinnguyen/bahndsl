package de.uniba.swt.dsl.generator;

public abstract class Element {
	
	public String hexString(long value) {
		return Util.toHexString(value);
	}
	
	public abstract String dumpYaml(String indent);
}

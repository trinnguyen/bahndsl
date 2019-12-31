package de.uniba.swt.dsl.common.models;

public class Util {
	
	public static String SINGLE_INDENT = "  ";
	
	public static String LINE_BREAK = "\n";
	
	public static String toHexString(long value) {
		return "0x" + (value < 16 ? "0" : "") + Long.toHexString(value).toUpperCase();
	}
	
	public static String increaseIndent(String value) {
		return value + SINGLE_INDENT;
	}
	
	public static StringBuilder appendLine(StringBuilder builder) {
		return builder.append(Util.LINE_BREAK);
	}
}
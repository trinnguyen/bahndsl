package de.uniba.swt.dsl.generator

class Util {
	
	public static val SINGLE_INDENT = "  "
	
	public static val LINE_BREAK = "\n"
	
	static def toHexString(long value) {
		return "0x" + (value < 16 ? "0" : "") + Long.toHexString(value).toUpperCase
	}
	
	def static increaseIndent(String value) {
		return value + SINGLE_INDENT
	}
	
	def static appendLine(StringBuilder builder) {
		return builder.append(Util.LINE_BREAK)
	}
}
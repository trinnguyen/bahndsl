package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;

@Data class Element {
	
	String id
	
	def hexString(long value) {
		Util.toHexString(value)
	}
	
	def dumpYaml(String indent) {
		return indent + "- id: " + id + "\n"
	}
	
	def appendLine(StringBuilder builder) {
		return builder.append(Util.LINE_BREAK)
	}
	
	def appendIndent(StringBuilder builder) {
		return builder.append(Util.SINGLE_INDENT)
	}
}
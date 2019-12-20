package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;

@Data class Aspect extends Element {
	long value
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).appendIndent.append("value: " + value.hexString).appendLine
		return builder.toString
	}
}
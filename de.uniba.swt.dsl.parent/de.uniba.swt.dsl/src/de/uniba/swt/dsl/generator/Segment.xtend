package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;

@Data class Segment extends Element {
	String boardId
	long address
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).append("  address: " + address.hexString).appendLine
		return builder.toString
	}
}
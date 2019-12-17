package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;
import java.util.Set

@Data class Signal extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).append("  number: " + number.hexString).appendLine
		return builder.toString
	}
}
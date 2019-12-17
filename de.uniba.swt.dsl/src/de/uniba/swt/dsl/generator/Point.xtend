package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;
import java.util.Set

@Data class Point extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).appendIndent.append("number: " + number.hexString).appendLine
		
		if (aspects !== null && aspects.size > 0) {
			builder.append(indent).appendIndent.append("aspects:").appendLine
			for (aspect : aspects) {
				builder.append(aspect.dumpYaml(Util.increaseIndent(Util.increaseIndent(indent))))
			}
		}
		
		if (initialAspect !== null) {
			builder.append(indent).appendIndent.append("initial: " + initialAspect.value.hexString).appendLine
		}
		
		return builder.toString
	}
}
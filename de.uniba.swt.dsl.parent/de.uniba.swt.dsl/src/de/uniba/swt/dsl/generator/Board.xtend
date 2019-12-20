package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;
import java.util.Set

@Data class Board extends Element {
	long uniqueId
	Set<BoardFeature> features
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		var findent = Util.increaseIndent(indent)
		builder.append(findent).append("unique-id: " + uniqueId.hexString).appendLine
		if (features !== null && features.size > 0) {
			builder.append(findent).append("features:").appendLine
			findent = Util.increaseIndent(findent)
			for (f: features) {
				builder.append(findent).append("- number: " + f.number.hexString).appendLine
				builder.append(findent).append("  value: " + f.value.hexString).appendLine
			}
		}
		return builder.toString
	}
}
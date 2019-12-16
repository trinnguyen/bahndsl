package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;

@Data class TrainPeripheral extends Element {
	int bit
	int initial
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		
		val fintent = Util.increaseIndent(indent)
		builder.append(fintent).append("bit: " + bit).appendLine
		builder.append(fintent).append("initial: " + initial).appendLine
		return builder.toString
	}
}
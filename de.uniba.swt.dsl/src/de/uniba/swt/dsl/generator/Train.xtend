package de.uniba.swt.dsl.generator
import org.eclipse.xtend.lib.annotations.Data;
import java.util.Set

@Data class Train extends Element {
	long dccAddress
	int speedSteps
	Set<Integer> calibration
	Set<TrainPeripheral> peripherals
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		
		var findent = Util.increaseIndent(indent)
		builder.append(findent).append("dcc-address: " + dccAddress.hexString).appendLine
		builder.append(findent).append("dcc-speed-steps: " + speedSteps).appendLine
		
		if (calibration !== null && calibration.size > 0) {
			builder.append(findent).append("calibration: ").appendLine
			for (cal : calibration) {
				builder.append(findent).appendIndent.append("- " + cal).appendLine
			}
		}
		
		if (peripherals !== null && peripherals.size > 0) {
			builder.append(findent).append("peripherals: ").appendLine
			for (per : peripherals) {
				builder.append(per.dumpYaml(Util.increaseIndent(findent)))
			}
		}
		
		return builder.toString
	}
}
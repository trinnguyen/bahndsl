package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter @AllArgsConstructor 
class Train extends Element {
	private String id;
	private long dccAddress;
	private int speedSteps;
	private List<Integer> calibration;
	private List<TrainPeripheral> peripherals;
	
	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		
		String findent = Util.increaseIndent(indent);
		builder.append(findent).append("dcc-address: " + hexString(dccAddress)).append(Util.LINE_BREAK);
		builder.append(findent).append("dcc-speed-steps: " + speedSteps).append(Util.LINE_BREAK);
		
		if (calibration != null && calibration.size() > 0) {
			builder.append(findent).append("calibration: ").append(Util.LINE_BREAK);
			for (Integer cal : calibration) {
				builder.append(findent).append(Util.SINGLE_INDENT).append("- " + cal).append(Util.LINE_BREAK);
			}
		}
		
		if (peripherals != null && peripherals.size() > 0) {
			builder.append(findent).append("peripherals: ").append(Util.LINE_BREAK);
			for (TrainPeripheral per : peripherals) {
				builder.append(per.dumpYaml(Util.increaseIndent(findent)));
			}
		}
		
		return builder.toString();
	}
}
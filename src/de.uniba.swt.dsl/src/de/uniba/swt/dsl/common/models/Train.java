package de.uniba.swt.dsl.common.models;

import java.util.List;

public class Train extends Element {
	private String id;
	private String dccAddress;
	private int speedSteps;
	private List<Integer> calibration;
	private List<TrainPeripheral> peripherals;

	public Train(String id, String dccAddress, int speedSteps, List<Integer> calibration, List<TrainPeripheral> peripherals) {
		this.id = id;
		this.dccAddress = dccAddress;
		this.speedSteps = speedSteps;
		this.calibration = calibration;
		this.peripherals = peripherals;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: ").append(id).append(Util.LINE_BREAK);
		
		String findent = Util.increaseIndent(indent);
		builder.append(findent).append("dcc-address: ").append(dccAddress).append(Util.LINE_BREAK);
		builder.append(findent).append("dcc-speed-steps: ").append(speedSteps).append(Util.LINE_BREAK);
		
		if (calibration != null && calibration.size() > 0) {
			builder.append(findent).append("calibration: ").append(Util.LINE_BREAK);
			for (Integer cal : calibration) {
				builder.append(findent).append(Util.SINGLE_INDENT).append("- ").append(cal).append(Util.LINE_BREAK);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDccAddress() {
		return dccAddress;
	}

	public void setDccAddress(String dccAddress) {
		this.dccAddress = dccAddress;
	}

	public int getSpeedSteps() {
		return speedSteps;
	}

	public void setSpeedSteps(int speedSteps) {
		this.speedSteps = speedSteps;
	}

	public List<Integer> getCalibration() {
		return calibration;
	}

	public void setCalibration(List<Integer> calibration) {
		this.calibration = calibration;
	}

	public List<TrainPeripheral> getPeripherals() {
		return peripherals;
	}

	public void setPeripherals(List<TrainPeripheral> peripherals) {
		this.peripherals = peripherals;
	}
}
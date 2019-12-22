package de.uniba.swt.dsl.generator;

import java.util.List;

class Train extends Element {
	private String id;
	private long dccAddress;
	private int speedSteps;
	private List<Integer> calibration;
	private List<TrainPeripheral> peripherals;

	public Train(String id, long dccAddress, int speedSteps, List<Integer> calibration, List<TrainPeripheral> peripherals) {
		this.id = id;
		this.dccAddress = dccAddress;
		this.speedSteps = speedSteps;
		this.calibration = calibration;
		this.peripherals = peripherals;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDccAddress() {
		return dccAddress;
	}

	public void setDccAddress(long dccAddress) {
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
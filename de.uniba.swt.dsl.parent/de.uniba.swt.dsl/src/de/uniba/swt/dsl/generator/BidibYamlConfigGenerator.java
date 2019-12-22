package de.uniba.swt.dsl.generator;

import java.util.List;

class BidibYamlConfigGenerator {
	
	public String dumpBoardConfig(String name, List<Board> boards) {
		// boards
		StringBuilder builder = new StringBuilder("# BiDiB board configuration: " + name).append(Util.LINE_BREAK);
		builder.append("boards:").append(Util.LINE_BREAK);
		if (boards != null) {
			for (Board b: boards) {
				builder.append(b.dumpYaml(Util.SINGLE_INDENT));
			}
		}
		return builder.toString();
	}
	
	String dumpTrackConfig(NetworkModel network) {
		StringBuilder builder = new StringBuilder("# Track configuration: " + network.name).append(Util.LINE_BREAK);
		builder.append("boards:").append(Util.LINE_BREAK);
		
		// segments
		List<Segment> segments = network.segments;
		if (segments != null && segments.size() > 0) {
			String indent = Util.SINGLE_INDENT;
			builder.append(indent).append("- id: " + segments.get(0).getBoardId()).append(Util.LINE_BREAK);
			builder.append(indent).append(indent).append("segments: ").append(Util.LINE_BREAK);
			for (Segment s: segments) {
				builder.append(s.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		// signals
		List<Signal> signals = network.signals;
		if (signals != null && signals.size() > 0) {
			String indent = Util.SINGLE_INDENT;
			builder.append(indent).append("- id: " + signals.get(0).getBoardId()).append(Util.LINE_BREAK);
			builder.append(indent).append(indent).append("signals-board: ").append(Util.LINE_BREAK);
			for (Signal s: signals) {
				builder.append(s.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		// points
		List<Point> points = network.points;
		if (points != null && points.size() > 0) {
			String indent = Util.SINGLE_INDENT;
			builder.append(indent).append("- id: " + points.get(0).getBoardId()).append(Util.LINE_BREAK);
			builder.append(indent).append(indent).append("points-board: ").append(Util.LINE_BREAK);
			for (Point p: points) {
				builder.append(p.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		return builder.toString();
	}
	
	String dumpTrainConfig(String name, List<Train> trains) {
		// trains
		StringBuilder builder = new StringBuilder("# Train configuration: " + name).append(Util.LINE_BREAK);
		builder.append("trains:").append(Util.LINE_BREAK);
		if (trains != null) {
			for (Train t: trains) {
				builder.append(t.dumpYaml(Util.SINGLE_INDENT));
			}			
		}

		return builder.toString();
	}
}
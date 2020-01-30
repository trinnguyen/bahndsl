package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.common.models.*;

import java.util.List;

public class BidibYamlConfigGenerator {
	
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
	
	public String dumpTrackConfig(NetworkModel network) {
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
		for (SignalBoard signalBoard : network.signalBoards) {
			String indent = Util.SINGLE_INDENT;
			builder.append(indent).append("- id: " + signalBoard.boardName).append(Util.LINE_BREAK);
			builder.append(indent).append(indent).append("signals-board: ").append(Util.LINE_BREAK);
			for (Signal s: signalBoard.signals) {
				builder.append(s.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		// points
		for (PointBoard pointBoard : network.pointBoards) {
			String indent = Util.SINGLE_INDENT;
			builder.append(indent).append("- id: " + pointBoard.boardName).append(Util.LINE_BREAK);
			builder.append(indent).append(indent).append("points-board: ").append(Util.LINE_BREAK);
			for (Point point : pointBoard.points) {
				builder.append(point.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		return builder.toString();
	}
	
	public String dumpTrainConfig(String name, List<Train> trains) {
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
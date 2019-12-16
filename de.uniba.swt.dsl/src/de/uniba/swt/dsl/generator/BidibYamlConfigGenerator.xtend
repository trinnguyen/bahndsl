package de.uniba.swt.dsl.generator

import java.util.Set

class BidibYamlConfigGenerator {
	
	def dumpBoardConfig(String name, Set<Board> boards) {
		// boards
		val builder = new StringBuilder("# BiDiB board configuration: " + name).appendLine
		builder.append("boards:").appendLine
		if (boards !== null) {
			for (b: boards) {
				builder.append(b.dumpYaml(Util.SINGLE_INDENT))
			}
		}
		return builder.toString
	}
	
	def dumpTrackConfig(String name, Set<Segment> segments, Set<Signal> signals, Set<Point> points) {
		val builder = new StringBuilder("# Track configuration: " + name).appendLine
		builder.append("boards:").appendLine
		
		// segments
		if (segments !== null && segments.size > 0) {
			val indent = Util.SINGLE_INDENT
			builder.append(indent).append("- id: " + segments.get(0).boardId).appendLine
			builder.append(indent).append(indent).append("segments: ").appendLine
			for (s: segments) {
				builder.append(s.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		// signals
		if (signals !== null && signals.size > 0) {
			val indent = Util.SINGLE_INDENT
			builder.append(indent).append("- id: " + signals.get(0).boardId).appendLine
			builder.append(indent).append(indent).append("signals-board: ").appendLine
			for (s: signals) {
				builder.append(s.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		// points
		if (points !== null && points.size > 0) {
			val indent = Util.SINGLE_INDENT
			builder.append(indent).append("- id: " + points.get(0).boardId).appendLine
			builder.append(indent).append(indent).append("points-board: ").appendLine
			for (p: points) {
				builder.append(p.dumpYaml(indent + Util.SINGLE_INDENT + Util.SINGLE_INDENT));
			}
		}
		
		return builder.toString
	}
	
	def dumpTrainConfig(String name, Set<Train> trains) {
		// trains
		val builder = new StringBuilder("# Train configuration: " + name).appendLine
		builder.append("trains:").appendLine
		if (trains !== null) {
			for (t: trains) {
				builder.append(t.dumpYaml(Util.SINGLE_INDENT))
			}			
		}

		return builder.toString
	}
	
	def appendLine(StringBuilder builder) {
		return builder.append("\n")
	}
}
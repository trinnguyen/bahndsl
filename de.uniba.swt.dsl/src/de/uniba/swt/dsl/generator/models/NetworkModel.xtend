package de.uniba.swt.dsl.generator.models

import java.util.Set
import org.eclipse.xtend.lib.annotations.Data;

class NetworkModel {
	public String name
	public Set<Aspect> aspects
	public Set<Board> boards
	public Set<Segment> segments
	public Set<Signal> signals
	public Set<Point> points
	public Set<Train> trains
	public Set<LayoutConnector> connectors
	public PlatformConfig platformConfig
}

@Data class Element {
	String id
	
	def hexString(long value) {
		Util.toHexString(value)
	}
	
	def dumpYaml(String indent) {
		return indent + "- id: " + id + "\n"
	}
}

@Data class Board extends Element {
	long uniqueId
	Set<BoardFeature> features
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).append("\n");
		builder.append(indent).append("  unique-id: " + uniqueId.hexString).append("\n")
		if (features !== null && features.size > 0) {
			val findent = indent + "  "
			for (f: features) {
				builder.append(findent).append("- number: " + f.number.hexString).append("\n")
				builder.append(findent).append("  value: " + f.value.hexString).append("\n")
			}
		}
		return builder.toString
	}
}

@Data class BoardFeature {
	long number
	long value
}

@Data class Segment extends Element {
	String boardId
	long address
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).append("\n");
		builder.append(indent).append("  address: " + address.hexString).append("\n");
		return builder.toString
	}
}

@Data class Signal extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
}

@Data class Point extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
}

@Data class Aspect extends Element {
	long value
}

@Data class Train extends Element {
	long dccAddress
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).append("\n");
		builder.append(indent).append("  dcc-address: " + dccAddress.hexString).append("\n");
		return builder.toString
	}
}

@Data class LayoutConnector {
	
}

@Data class PlatformConfig {
	
}

@Data class Route {
	
}

@Data class InterlockingTable {
	
}
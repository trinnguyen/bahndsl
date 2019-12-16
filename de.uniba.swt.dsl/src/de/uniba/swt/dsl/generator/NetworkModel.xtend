package de.uniba.swt.dsl.generator

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
	
	def appendLine(StringBuilder builder) {
		return builder.append(Util.LINE_BREAK)
	}
	
	def appendIndent(StringBuilder builder) {
		return builder.append(Util.SINGLE_INDENT)
	}
}


@Data class Segment extends Element {
	String boardId
	long address
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).append("  address: " + address.hexString).appendLine
		return builder.toString
	}
}

@Data class Signal extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).append("  number: " + number.hexString).appendLine
		return builder.toString
	}
}

@Data class Point extends Element {
	String boardId
	long number
	Set<Aspect> aspects
	Aspect initialAspect
	
	override dumpYaml(String indent) {
		val builder = new StringBuilder
		builder.append(indent).append("- id: " + id).appendLine
		builder.append(indent).append("  number: " + number.hexString).appendLine
		return builder.toString
	}
}

@Data class Aspect extends Element {
	long value
}



@Data class LayoutConnector {
	
}

@Data class PlatformConfig {
	
}

@Data class Route {
	
}

@Data class InterlockingTable {
	
}
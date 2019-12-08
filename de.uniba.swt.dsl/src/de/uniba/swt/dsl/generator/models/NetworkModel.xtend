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
}

@Data class Board extends Element {
	long uniqueId
	Set<BoardFeature> features
}

@Data class BoardFeature {
	long number
	long value
}

@Data class Segment extends Element {
	String boardId
	long address
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
}

@Data class LayoutConnector {
	
}

@Data class PlatformConfig {
	
}

@Data class Route {
	
}

@Data class InterlockingTable {
	
}
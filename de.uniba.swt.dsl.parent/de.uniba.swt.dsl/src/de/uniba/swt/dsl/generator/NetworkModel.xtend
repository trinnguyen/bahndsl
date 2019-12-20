package de.uniba.swt.dsl.generator

import java.util.Set
import org.eclipse.xtend.lib.annotations.Data

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


@Data class LayoutConnector {
	
}

@Data class PlatformConfig {
	
}

@Data class Route {
	
}

@Data class InterlockingTable {
	
}
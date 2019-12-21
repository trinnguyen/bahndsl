package de.uniba.swt.dsl.generator;

import java.util.List;
import lombok.Data;

class NetworkModel {
	public String name;

	public List<Aspect> aspects;

	public List<Board> boards;

	public List<Segment> segments;

	public List<Signal> signals;

	public List<Point> points;

	public List<Train> trains;

	public List<LayoutConnector> connectors;

	public PlatformConfig platformConfig;
}


@Data class LayoutConnector {
	
}

@Data class PlatformConfig {
	
}

@Data class Route {
	
}

@Data class InterlockingTable {
	
}
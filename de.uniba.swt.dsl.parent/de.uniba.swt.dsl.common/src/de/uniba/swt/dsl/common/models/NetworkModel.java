package de.uniba.swt.dsl.common.models;

import java.util.List;

public class NetworkModel {
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


class LayoutConnector {
	
}

class PlatformConfig {
	
}

class Route {
	
}

class InterlockingTable {
	
}
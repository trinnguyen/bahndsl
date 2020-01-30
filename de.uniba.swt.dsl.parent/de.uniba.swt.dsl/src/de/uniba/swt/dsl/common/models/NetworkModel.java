package de.uniba.swt.dsl.common.models;

import java.util.ArrayList;
import java.util.List;

public class NetworkModel {
	public String name;

	public List<Aspect> aspects = new ArrayList<>();

	public List<Board> boards = new ArrayList<>();

	public List<Segment> segments = new ArrayList<>();

	public List<SignalBoard> signalBoards = new ArrayList<>();

	public List<PointBoard> pointBoards = new ArrayList<>();

	public List<Train> trains = new ArrayList<>();

	public List<LayoutConnector> connectors = new ArrayList<>();

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
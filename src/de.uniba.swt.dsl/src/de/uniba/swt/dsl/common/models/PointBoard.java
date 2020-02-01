package de.uniba.swt.dsl.common.models;

import java.util.List;

public class PointBoard {
	public PointBoard(String boardName, List<Point> points) {
		this.boardName = boardName;
		this.points = points;
	}

	public String boardName;

	public List<Point> points;
}

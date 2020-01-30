package de.uniba.swt.dsl.common.models;

import java.util.List;

public class SignalBoard {
	public SignalBoard(String boardName, List<Signal> signals) {
		this.boardName = boardName;
		this.signals = signals;
	}

	public String boardName;

	public List<Signal> signals;
}

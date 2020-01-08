package de.uniba.swt.dsl.common.models;

import java.util.List;

public class Signal extends Element {
	private String id;
	private String boardId;
	private long number;
	private List<Aspect> aspects;
	private Aspect initialAspect;

	public Signal(String id, String boardId, long number, List<Aspect> aspects, Aspect initialAspect) {
		this.id = id;
		this.boardId = boardId;
		this.number = number;
		this.aspects = aspects;
		this.initialAspect = initialAspect;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append("  number: " + hexString(number)).append(Util.LINE_BREAK);
		
		if (aspects != null && aspects.size() > 0) {
			builder.append(indent).append(Util.SINGLE_INDENT).append("aspects:").append(Util.LINE_BREAK);
			for (Aspect aspect : aspects) {
				builder.append(aspect.dumpYaml(Util.increaseIndent(Util.increaseIndent(indent))));
			}
		}
		
		if (initialAspect != null) {
			builder.append(indent).append(Util.SINGLE_INDENT).append("initial: " + hexString(initialAspect.getValue())).append(Util.LINE_BREAK);
		}
		
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

	public Aspect getInitialAspect() {
		return initialAspect;
	}

	public void setInitialAspect(Aspect initialAspect) {
		this.initialAspect = initialAspect;
	}
}
package de.uniba.swt.dsl.common.models;

public class Segment extends Element {
	private String id;
	private String boardId;
	private String address;

	public Segment(String id, String boardId, String address) {
		this.id = id;
		this.boardId = boardId;
		this.address = address;
	}

	@Override
	public String dumpYaml(String indent) {
		return indent + "- id: " + id + Util.LINE_BREAK +
				indent + "  address: " + address + Util.LINE_BREAK;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
package de.uniba.swt.dsl.generator;

class Segment extends Element {
	private String id;
	private String boardId;
	private long address;

	public Segment(String id, String boardId, long address) {
		this.id = id;
		this.boardId = boardId;
		this.address = address;
	}

	@Override
	public String dumpYaml(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("- id: " + id).append(Util.LINE_BREAK);
		builder.append(indent).append("  address: " + hexString(address)).append(Util.LINE_BREAK);
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

	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}
}
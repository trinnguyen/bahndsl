package de.uniba.swt.dsl.util;

public class BahnException extends RuntimeException {
	
	private static final long serialVersionUID = -4218933982917832302L;

	public BahnException() {
		super();
	}

	public BahnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BahnException(String message, Throwable cause) {
		super(message, cause);
	}

	public BahnException(String message) {
		super(message);
	}

	public BahnException(Throwable cause) {
		super(cause);
	}
}

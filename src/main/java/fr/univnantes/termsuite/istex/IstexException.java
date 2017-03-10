package fr.univnantes.termsuite.istex;

public class IstexException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IstexException() {
		super();
	}

	public IstexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IstexException(String message, Throwable cause) {
		super(message, cause);
	}

	public IstexException(String message) {
		super(message);
	}

	public IstexException(Throwable cause) {
		super(cause);
	}
}

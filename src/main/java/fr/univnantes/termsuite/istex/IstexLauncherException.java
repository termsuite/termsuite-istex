package fr.univnantes.termsuite.istex;

public class IstexLauncherException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IstexLauncherException() {
		super();
	}

	public IstexLauncherException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IstexLauncherException(String message, Throwable cause) {
		super(message, cause);
	}

	public IstexLauncherException(String message) {
		super(message);
	}

	public IstexLauncherException(Throwable cause) {
		super(cause);
	}
}

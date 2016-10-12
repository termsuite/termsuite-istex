package fr.univnantes.termsuite.istex;

public class IstexLauncherException extends RuntimeException {

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

	public IstexLauncherException(String format, Object... args) {
		super(String.format(format, args));
	}

	public IstexLauncherException(Throwable cause) {
		super(cause);
	}
}

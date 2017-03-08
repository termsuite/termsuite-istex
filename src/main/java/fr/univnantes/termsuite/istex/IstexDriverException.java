package fr.univnantes.termsuite.istex;

import fr.univnantes.termsuite.model.Lang;

public class IstexDriverException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Lang lang = null;
	private String uri;
	
	public IstexDriverException(Lang lang, String uri, Throwable t) {
		super("Could not retrieve document " + uri, t);
		this.lang = lang;
		this.uri = uri;
	}

	public IstexDriverException(Lang lang, String uri) {
		super("Could not retrieve document " + uri);
		this.lang = lang;
		this.uri = uri;
	}
	
	public IstexDriverException(Throwable t) {
		super(t);
	}
	
	public IstexDriverException(String msg, Throwable t) {
		super(msg, t);
	}

	
	public Lang getLang() {
		return lang;
	}
	
	public String getUri() {
		return uri;
	}
}

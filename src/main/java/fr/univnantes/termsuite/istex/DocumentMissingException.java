package fr.univnantes.termsuite.istex;

import eu.project.ttc.engines.desc.Lang;

public class DocumentMissingException extends RuntimeException {
	
	private Lang lang = null;
	private String uri;
	
	public DocumentMissingException(Lang lang, String uri) {
		super("Could not retrieve document " + uri);
		this.lang = lang;
		this.uri = uri;
	}

	public Lang getLang() {
		return lang;
	}
	
	public String getUri() {
		return uri;
	}
}

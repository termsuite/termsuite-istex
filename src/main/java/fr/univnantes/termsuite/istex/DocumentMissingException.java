package fr.univnantes.termsuite.istex;

public class DocumentMissingException extends RuntimeException {
	public DocumentMissingException(String documentURL) {
		super("Could not retrieve document " + documentURL);
	}
}

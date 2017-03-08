package fr.univnantes.termsuite.istex.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.univnantes.termsuite.istex.IstexDriverException;
import fr.univnantes.termsuite.istex.model.IstexDocument;

public class IstexService {

	@Inject @Named("istex.api")
	private URI api;

	
	public URL getDocumentURL(String documentId) {
		String documentPath = String.format("/document/%s/", documentId);
		URL documentURL;
		try {
			documentURL = new URL(api.toURL(), documentPath);
			return documentURL;
		} catch (MalformedURLException e) {
			throw new IstexDriverException(e);
		}
	}

	public String getResponseBody(URL documentURL) throws IOException {
		try(InputStream	openStream = documentURL.openStream();
				Scanner scanner = new Scanner(openStream);) {
			Scanner s = scanner.useDelimiter("\\A");
			String json = s.hasNext() ? s.next() : "";
			return json;
		}
	}

	public IstexDocument parseDocumentFromJson(String json) {
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		try {
			return mapper.readValue(json, IstexDocument.class);
		} catch (Exception e) {
			throw new IstexDriverException("Could not parse istex document json", e);
		}
	}

	public IstexDocument getDocument(URL documentURL) {
		InputStream openStream;
		try {
			openStream = documentURL.openStream();
			Scanner scanner = new Scanner(openStream);
			Scanner s = scanner.useDelimiter("\\A");
			String json = s.hasNext() ? s.next() : "";
			scanner.close();
			return parseDocumentFromJson(json);
		} catch (IOException e) {
			throw new IstexDriverException(e);
		}

	}
}

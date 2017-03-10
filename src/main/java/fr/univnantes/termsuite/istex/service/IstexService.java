package fr.univnantes.termsuite.istex.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.univnantes.termsuite.istex.IstexDriverException;
import fr.univnantes.termsuite.istex.IstexException;
import fr.univnantes.termsuite.istex.json.RawType;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.model.Document;

public class IstexService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IstexService.class);
	
	@Inject @Named("istex.api")
	private URI api;

	
	public URL makeDocumentURL(String documentId) {
		String documentPath = String.format("/document/%s/", documentId);
		URL documentURL;
		try {
			documentURL = new URL(api.toURL(), documentPath);
			return documentURL;
		} catch (MalformedURLException e) {
			throw new IstexDriverException(e);
		}
	}
	
	public Optional<URL> getDocumentFulltextURL(Document doc) {
		URL documentURL2;
		try {
			documentURL2 = new URL(doc.getUrl());
		} catch (MalformedURLException e) {
			throw new IstexDriverException(e);
		}
		IstexDocument document = getDocument(documentURL2);
		Map<RawType, URL> fulltext = document.getFulltext();
		if(fulltext == null || !fulltext.containsKey(RawType.TXT))
			return Optional.empty();
		else
			return Optional.of(fulltext.get(RawType.TXT));
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

	/**
	 * 
	 * Gets the content of the txt fulltext of a document if it 
	 * is available in Istex corpus.
	 * 
	 * @param document
	 * 			The TermSuite document object
	 * @return
	 * 			the fulltext , or {@link Optional#empty()} if none is available.
	 */
	public Optional<String> getFullText(Document document) {
		try {
			Optional<URL> fulltext = getDocumentFulltextURL(document);
			if(fulltext.isPresent()) {
				LOGGER.debug("Downloading fulltext for document {}", document);
				String responseBody = getResponseBody(fulltext.get());
				return Optional.of(responseBody);
			}
			else
				return Optional.empty();
		} catch(Exception e) {
			throw new IstexException(e);
		}
	}
}

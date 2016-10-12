package fr.univnantes.termsuite.istex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import eu.project.ttc.api.Document;
import eu.project.ttc.api.TerminoExtractor;
import eu.project.ttc.api.TerminoExtractor.ContextualizerMode;
import eu.project.ttc.api.TerminoFilterConfig;
import eu.project.ttc.engines.desc.Lang;
import eu.project.ttc.history.TermHistory;
import eu.project.ttc.models.TermIndex;
import eu.project.ttc.utils.FileUtils;

public class IstexTerminoExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(IstexTerminoExtractor.class);
	
	private TerminoExtractor extractor;
	
	
	private IstexTerminoExtractor(TerminoExtractor extractor) {
		super();
		this.extractor = extractor;
	}

	public TermIndex execute() {
		return extractor.execute();
	}

	public TerminoExtractor setTreeTaggerHome(String treeTaggerHome) {
		return extractor.setTreeTaggerHome(treeTaggerHome);
	}

	public TerminoExtractor usingCustomResources(String resourceDir) {
		return extractor.usingCustomResources(resourceDir);
	}

	public TerminoExtractor useContextualizer(int scope, ContextualizerMode contextualizerMode) {
		return extractor.useContextualizer(scope, contextualizerMode);
	}

	public TerminoExtractor preFilter(TerminoFilterConfig filterConfig) {
		return extractor.preFilter(filterConfig);
	}

	public TerminoExtractor dynamicMaxSizeFilter(int maxTermIndexSize) {
		return extractor.dynamicMaxSizeFilter(maxTermIndexSize);
	}

	public TerminoExtractor postFilter(TerminoFilterConfig filterConfig) {
		return extractor.postFilter(filterConfig);
	}

	public TerminoExtractor setWatcher(TermHistory history) {
		return extractor.setWatcher(history);
	}
	
	public static IstexTerminoExtractor fromIdFile(Lang lang, File idFile) {
		return fromIdFile(lang, idFile, Charset.defaultCharset());
	}
	
	public static IstexTerminoExtractor fromIdFile(Lang lang, File idFile, Charset charset) {
		List<String> uncommentedLines;
		try {
			uncommentedLines = FileUtils.getUncommentedLines(idFile, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return fromDocumentIds(lang, uncommentedLines.toArray(new String[uncommentedLines.size()]));
	}


	public static IstexTerminoExtractor fromDocumentIds(Lang lang, Iterable<String> split) {
		ArrayList<String> l = Lists.newArrayList(split);
		return fromDocumentIds(lang, l.toArray(new String[l.size()]));
	}
	
	public static IstexTerminoExtractor fromDocumentIds(Lang lang, String... ids) {
		Stream<Document> documentStream = Lists.newArrayList(ids).stream().map( id -> {
			String documentURL = String.format("https://api.istex.fr/document/%s/", id);
			try(
					InputStream openStream = new URL(documentURL).openStream();
					Scanner scanner = new Scanner(openStream);) {				
				Scanner s = scanner.useDelimiter("\\A");
				String text = s.hasNext() ? s.next() : "";
				return new Document(lang, documentURL, text);
			} catch (IOException e) {
				LOGGER.error(String.format("Could not retrieve ISTEX document %s.", documentURL));
				throw new DocumentMissingException(documentURL);
			}
		}).filter(doc -> doc != null);
		return new IstexTerminoExtractor(TerminoExtractor.fromDocumentStream(lang, documentStream, ids.length));
	}


}

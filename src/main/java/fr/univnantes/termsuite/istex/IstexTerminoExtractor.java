package fr.univnantes.termsuite.istex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
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
		LOGGER.info("Executing the pipeline.");
		Stopwatch sw = Stopwatch.createStarted();
		TermIndex termIndex = extractor.execute();
		sw.stop();
		LOGGER.info("Extracted TermIndex {} on {} tokens from {} documents.", 
				termIndex.getName(),
				termIndex.getWordAnnotationsNum(),
				termIndex.getDocuments().size()
				);
		LOGGER.info("The execution of pipeline has finished in {}.", sw.toString());
		return termIndex;
	}

	public IstexTerminoExtractor setTreeTaggerHome(String treeTaggerHome) {
		extractor.setTreeTaggerHome(treeTaggerHome);
		return this;
	}

	public IstexTerminoExtractor usingCustomResources(String resourceDir) {
		extractor.usingCustomResources(resourceDir);
		return this;
	}

	public IstexTerminoExtractor useContextualizer(int scope, ContextualizerMode contextualizerMode) {
		extractor.useContextualizer(scope, contextualizerMode);
		return this;
	}

	public IstexTerminoExtractor preFilter(TerminoFilterConfig filterConfig) {
		extractor.preFilter(filterConfig);
		return this;
	}

	public IstexTerminoExtractor dynamicMaxSizeFilter(int maxTermIndexSize) {
		extractor.dynamicMaxSizeFilter(maxTermIndexSize);
		return this;
	}

	public IstexTerminoExtractor postFilter(TerminoFilterConfig filterConfig) {
		extractor.postFilter(filterConfig);
		return this;
	}

	public IstexTerminoExtractor setWatcher(TermHistory history) {
		extractor.setWatcher(history);
		return this;
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

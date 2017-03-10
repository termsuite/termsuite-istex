package fr.univnantes.termsuite.istex;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import fr.univnantes.termsuite.api.Preprocessor;
import fr.univnantes.termsuite.framework.modules.TermSuiteModule;
import fr.univnantes.termsuite.istex.service.AbstractCorpusFactory;
import fr.univnantes.termsuite.istex.service.FulltextCorpusFactory;
import fr.univnantes.termsuite.istex.service.IstexModule;
import fr.univnantes.termsuite.model.Lang;

public class TermSuiteIstex {
	public static Preprocessor preprocessor() {
		return termSuiteInjector().getInstance(Preprocessor.class);
	}
	
	private static Injector termSuiteInjector() {
		return Guice.createInjector(Modules.override(new TermSuiteModule())
				.with(new IstexModule()));
	}
	
	public static List<String> readDocumentIds(Path isFile) throws IOException {
		return Files.readAllLines(isFile)
				.stream()
				.map(line -> line.trim().replaceFirst("#.*$", ""))
				.filter(line -> !line.isEmpty())
				.map(line -> line.replaceAll("\\s+", ""))
				.collect(toList());
	}

	public static IstexCorpus createAbstractIstexCorpus(Lang lang, List<String> documentIds) {
		Injector injector = istexInjector();
		return injector.getInstance(AbstractCorpusFactory.class).createCorpus(
				lang, 
				documentIds);
	}

	public static IstexCorpus createFulltextIstexCorpus(Lang lang, List<String> documentIds, boolean failOnMissingFulltext) {
		Injector injector = istexInjector();
		return injector.getInstance(FulltextCorpusFactory.class).createCorpus(
				lang, 
				documentIds,
				failOnMissingFulltext);
	}

	/**
	 * Alias for {@link #createFulltextIstexCorpus(Lang, List, <code>true</code>)}
	 * 
	 * @param lang
	 * @param documentIds
	 * @return 
	 * 			the created istex corpus
	 */
	public static IstexCorpus createFulltextIstexCorpus(Lang lang, List<String> documentIds) {
		return createFulltextIstexCorpus(lang, documentIds, true);
	}

	public static IstexCorpus createIstexCorpus(Lang lang, Mode mode, List<String> documentIds) {
		if(mode == Mode.ABSTRACT)
			return createAbstractIstexCorpus(lang, documentIds);
		else if(mode == Mode.FULLTEXT)
			return createFulltextIstexCorpus(lang, documentIds);
		else
			throw new IllegalArgumentException("Unknown istex corpus mode: " + mode);
	}

	public static Injector istexInjector() {
		return Guice.createInjector(new IstexModule());
	}

}

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
import fr.univnantes.termsuite.istex.service.CorpusFactory;
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

	
	public static IstexCorpus createIstexCorpus(Lang lang, List<String> documentIds) {
		Injector injector = istexInjector();
		CorpusFactory factory = injector.getInstance(CorpusFactory.class);
		IstexCorpus textCorpus = factory.create(
				lang, 
				documentIds);
		return textCorpus;
	}

	public static Injector istexInjector() {
		return Guice.createInjector(new IstexModule());
	}

}

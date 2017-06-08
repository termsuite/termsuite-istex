package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import fr.univnantes.termsuite.api.TermSuite;
import fr.univnantes.termsuite.istex.IstexCorpus;
import fr.univnantes.termsuite.istex.IstexPreprocessor;
import fr.univnantes.termsuite.istex.Mode;
import fr.univnantes.termsuite.istex.TermSuiteIstex;
import fr.univnantes.termsuite.istex.service.IstexModule;
import fr.univnantes.termsuite.model.IndexedCorpus;
import fr.univnantes.termsuite.model.Lang;

public class FunctionalTests {

	public static final String TEST_SID = "termsuite-test";
	private String treeTaggerHome;
	private Path idFile;
	private List<String> documentIds;
	private IstexCorpus istexCorpus;
	
	@Before
	public void setup() throws IOException {
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
		idFile = Paths.get("src", "test", "resources", "list-335.tt");
		documentIds = TermSuiteIstex.readDocumentIds(idFile);
	}

	public static Injector createIstexTestModule() {
		return Guice.createInjector(new IstexModule(TEST_SID));
	}

	@Test
	public void testAbstract()  {
		istexCorpus = TermSuiteIstex.createIstexCorpus(
				FunctionalTests.TEST_SID,
				Lang.EN, 
				Mode.ABSTRACT,
				documentIds
				);
		IndexedCorpus corpus = new IstexPreprocessor(TermSuite.preprocessor()
				.setTaggerPath(Paths.get(treeTaggerHome)))
				.toIndexedCorpus(istexCorpus)
		;
		
		assertThat(corpus.getTerminology().getTerms()).isNotEmpty();
	}
	
	@Test  @Ignore("Istex API is currently broken")
	public void testFulltext()  {
		istexCorpus = TermSuiteIstex.createIstexCorpus(
				FunctionalTests.TEST_SID,
				Lang.EN, 
				Mode.FULLTEXT,
				documentIds
				);

		IndexedCorpus corpus = new IstexPreprocessor(TermSuite.preprocessor()
				.setTaggerPath(Paths.get(treeTaggerHome)))
				.toIndexedCorpus(istexCorpus)
		;
		
		assertThat(corpus.getTerminology().getTerms()).isNotEmpty();
	}
}

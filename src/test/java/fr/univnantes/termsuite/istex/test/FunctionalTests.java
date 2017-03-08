package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.univnantes.termsuite.api.TermSuite;
import fr.univnantes.termsuite.istex.IstexCorpus;
import fr.univnantes.termsuite.istex.IstexPreprocessor;
import fr.univnantes.termsuite.istex.TermSuiteIstex;
import fr.univnantes.termsuite.model.IndexedCorpus;
import fr.univnantes.termsuite.model.Lang;

public class FunctionalTests {

	private String treeTaggerHome;
	private Path idFile;
	private List<String> documentIds;
	private IstexCorpus istexCorpus;
	
	@Before
	public void setup() throws IOException {
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
		idFile = Paths.get("src", "test", "resources", "list-335.tt");
		documentIds = TermSuiteIstex.readDocumentIds(idFile);
		istexCorpus = TermSuiteIstex.createIstexCorpus(
				Lang.EN, documentIds);
	}

	@Test
	public void testParallelProcessing()  {
		IndexedCorpus corpus = new IstexPreprocessor(TermSuite.preprocessor()
				.setTaggerPath(Paths.get(treeTaggerHome)))
				.toIndexedCorpus(istexCorpus)
		;
		
		assertThat(corpus.getTerminology().getTerms()).isNotEmpty();
	}
}

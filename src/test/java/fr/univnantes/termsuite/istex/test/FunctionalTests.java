package fr.univnantes.termsuite.istex.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import eu.project.ttc.engines.desc.Lang;
import eu.project.ttc.models.TermIndex;
import fr.univnantes.termsuite.istex.IstexTerminoExtractor;

public class FunctionalTests {

	private String treeTaggerHome;
	
	@Before
	public void setup() {
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
	}

	@Test
	public void testParallelProcessing() {
		TermIndex termIndex = IstexTerminoExtractor
			.fromIdFile(Lang.EN, new File("src/test/resources/list-335.tt"))
			.setTreeTaggerHome(treeTaggerHome)
			.execute();
	}
}

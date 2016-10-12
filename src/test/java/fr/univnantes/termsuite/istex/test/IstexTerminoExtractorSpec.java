package fr.univnantes.termsuite.istex.test;

import static org.hamcrest.CoreMatchers.endsWith;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import eu.project.ttc.engines.desc.Lang;
import eu.project.ttc.models.TermIndex;
import fr.univnantes.termsuite.istex.DocumentMissingException;
import fr.univnantes.termsuite.istex.IstexTerminoExtractor;

public class IstexTerminoExtractorSpec {
	
	private static String id1;
	private static String id2;
	private static String treeTaggerHome;

	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setup() {
		id1 = "F697EDBD85006E482CD1AC91DE9D40F6C629727A";
		id2 = "15101397F055B3A872D495F7405D0A3F3E195E0F";
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
	}

	
	@Test
	public void testOnNonExistingId() {
		thrown.expect(DocumentMissingException.class);
		thrown.expectMessage(endsWith("/non-existing-id/"));
		
		IstexTerminoExtractor
				.fromDocumentIds(Lang.EN, "non-existing-id")
				.setTreeTaggerHome(treeTaggerHome)
				.execute();
	}

	@Test
	public void testFromIdFile() {
		TermIndex termIndex = IstexTerminoExtractor
				.fromDocumentIds(Lang.EN, id1, id2)
				.setTreeTaggerHome(treeTaggerHome)
				.execute();
			
		Tests.testTermino(termIndex);
	}

	@Test
	public void testFromDocumentIds() {
		TermIndex termIndex = IstexTerminoExtractor
			.fromIdFile(Lang.EN, new File("src/test/resources/id-file"))
			.setTreeTaggerHome(treeTaggerHome)
			.execute();
		
		Tests.testTermino(termIndex);
	}
	
}

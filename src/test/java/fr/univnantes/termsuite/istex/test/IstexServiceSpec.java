package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.net.MalformedURLException;
import java.net.URL;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import fr.univnantes.termsuite.istex.json.RawType;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexModule;
import fr.univnantes.termsuite.istex.service.IstexService;
import fr.univnantes.termsuite.model.Lang;

public class IstexServiceSpec {

	
	IstexService service;
	
	@Before
	public void setup() {
		service = Guice.createInjector(new IstexModule()).getInstance(IstexService.class);
	}

	@Test
	public void testGetDocumentURL() throws MalformedURLException {
		assertThat(service.makeDocumentURL("toto").toString())
		.isEqualTo("https://api.istex.fr/document/toto/");
	}

	@Test
	public void testGetDocument() throws MalformedURLException {
		IstexDocument document = service.parseDocumentFromJson(Tests.readFile(Tests.DOC1_PATH));
		
		assertThat(document.getAbstract())
			.contains("This paper reports on the application of mutual information");

		assertThat(document.getTitle())
			.isEqualTo("Application of mutual information theory to fluid bed temperature and differential pressure signal analysis");

		assertThat(document.getCorpusName())
			.isEqualTo("elsevier");

		assertThat(document.getLang())
			.isEqualTo(Lang.EN);
		
		assertThat(document.getPublicationDate())
			.isEqualTo("1995");

		assertThat(document.getAuthors())
			.hasSize(3)
			.extracting("name", "affiliations")
			.contains(
				tuple("Ali Ihsan Karamavruc",
						Lists.newArrayList("Department of Mechanical Engineering, West Virginia University, Morgantown, WV 26506-6106, USA")),
				tuple("Nigel N. Clark",
						Lists.newArrayList("Department of Mechanical Engineering, West Virginia University, Morgantown, WV 26506-6106, USA")),
				tuple("J.S. Halow",
						Lists.newArrayList("US Department of Energy, Morgantown Energy Technology Center, PO Box 880, Morgantown, WV 26505, USA"))
			);

		assertThat(document.getSubjects())
			.hasSize(3)
			.contains(
					"Mutual information theory",
					"Fluidized beds",
					"Differential pressure"
				);

		assertThat(document.getFulltext())
			.hasSize(4)
			.containsEntry(RawType.PDF, new URL("https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/fulltext/pdf"))
			.containsEntry(RawType.TXT, new URL("https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/fulltext/txt"))
			.containsEntry(RawType.TEI, new URL("https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/fulltext/tei"))
			.containsEntry(RawType.ZIP, new URL("https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/fulltext/zip"))
			;
	}
}

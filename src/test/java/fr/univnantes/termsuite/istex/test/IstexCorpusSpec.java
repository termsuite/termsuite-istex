package fr.univnantes.termsuite.istex.test;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.inject.Guice;

import fr.univnantes.termsuite.istex.AbstractIstexCorpus;
import fr.univnantes.termsuite.istex.service.AbstractCorpusFactory;
import fr.univnantes.termsuite.istex.service.IstexModule;
import fr.univnantes.termsuite.model.Document;
import fr.univnantes.termsuite.model.Lang;

public class IstexCorpusSpec {
	
	private String id1 = Tests.DOC_ID1;
	private String id2 = Tests.DOC_ID2;
	private List<String> ids = Lists.newArrayList(id1, id2);
	AbstractCorpusFactory factory;
	AbstractIstexCorpus corpus;
	
	@Before
	public void setup() {
		factory = Guice.createInjector(new IstexModule()).getInstance(AbstractCorpusFactory.class);
		corpus = factory.createCorpus(Lang.EN, ids);
	}

	@Test
	public void testDocuments() throws MalformedURLException {
		List<Document> collect = corpus.documents().collect(toList());
		assertThat(collect)
			.hasSize(2)
			.extracting("url")
			.contains("https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/")
			.contains("https://api.istex.fr/document/15101397F055B3A872D495F7405D0A3F3E195E0F/")
			;
	}
	
	@Test
	public void testReadDocumentText() throws MalformedURLException {
		Document doc = new Document(Lang.EN, "https://api.istex.fr/document/F697EDBD85006E482CD1AC91DE9D40F6C629727A/");
		
		
		assertThat(corpus.readDocumentText(doc))
			.contains("Application of mutual information theory to fluid bed temperature and differential pressure signal analysis. This paper reports on the application of mutual information theory to the analysis of transient differential pressure and temperature signals from a fluidized bed. The signals were recorded around a heat transfer tube which was placed horizontally into a bubbling fluidized bed. The heat transfer tube was instrumented with fast response surface thermocouples and differential pressure sensors. Mutual information theory was used to identify the periodicity and the predictability of the local instantaneous differential pressure and temperature signals. It was also used to interpret the bubble-particle packet dynamics around the instrumented heat transfer tube. As theoretical and limiting cases, purely periodic and random signals were observed. The conventional signal processing tools such as autocorrelation, cross-correlation and fast Fourier transformation (FFT) were used as preliminary tools to analyze data. The qualitative similarities between the mutual information function and the autocorrelation function are shown. The first minimum of the mutual information function is used to reconstruct the phase portrait from the one-dimensional time series. It is suggested that if the first minimum of the mutual information function exists, then using the time derivative of the measured one-dimensional signal with the least number of bins provides a better time delay τ than using the measured signal directly..");
		
	}

}

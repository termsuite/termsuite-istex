package fr.univnantes.termsuite.istex;

import java.net.URL;
import java.util.concurrent.Callable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexService;

public class GetDocumentTask implements Callable<IstexDocument> {
	
	private URL documentURL;
	private IstexService istexService;

	@Inject
	public GetDocumentTask(
			IstexService istexService,
			@Assisted URL documentURL) {
		super();
		this.documentURL = documentURL;
		this.istexService = istexService;
	}

	public IstexDocument call() {
		return istexService.getDocument(documentURL);
	}
}
package fr.univnantes.termsuite.istex;

import java.net.URL;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import fr.univnantes.termsuite.api.TermSuiteException;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexService;
import fr.univnantes.termsuite.model.Document;
import fr.univnantes.termsuite.model.Lang;

public class AbstractIstexCorpus extends IstexCorpus {
	
	@Inject
	public AbstractIstexCorpus(
			IstexService istexService,
			@Assisted Lang lang,
			@Assisted List<String> documentIds
			) {
		super();
		this.lang = lang;
		this.documentIds = documentIds;
		this.istexService = istexService;
	}

	@Override
	public String readDocumentText(Document doc) {
		IstexDocument istexDocument;
		try {
			istexDocument = istexCache.get(new URL(doc.getUrl()));
			return String.format("%s. %s.", istexDocument.getTitle(), istexDocument.getAbstract());
		} catch (Exception e) {
			throw new TermSuiteException(e);
		}
	}

	@Override
	public int getNbDocuments() {
		return -1;
	}

	@Override
	public long getTotalSize() {
		return -1;
	}
}

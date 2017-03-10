package fr.univnantes.termsuite.istex;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import fr.univnantes.termsuite.api.TermSuiteException;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexService;
import fr.univnantes.termsuite.model.Document;
import fr.univnantes.termsuite.model.Lang;

public class FulltextIstexCorpus extends IstexCorpus {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FulltextIstexCorpus.class);
	
	private boolean failOnMissingFulltext = true;

	@Inject
	public FulltextIstexCorpus(
			IstexService istexService,
			@Assisted Lang lang,
			@Assisted List<String> documentIds,
			@Assisted boolean failOnMissingFulltext
			) {
		this(istexService, lang, documentIds);
		this.failOnMissingFulltext = failOnMissingFulltext;
	}
	
	public FulltextIstexCorpus(
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
		try {
			Optional<String> fullText = istexService.getFullText(doc);
			if(fullText.isPresent())
				return fullText.get();
			else {
				if(failOnMissingFulltext)
					throw new IstexException("No fulltext available for document " + doc.getUrl());
				else {
					LOGGER.warn("No fulltext available for document {}. Backtracking to abstract.");
					IstexDocument istexDocument = istexCache.get(new URL(doc.getUrl()));
					return String.format("%s. %s.", istexDocument.getTitle(), istexDocument.getAbstract());
				}
			}
		} catch (Exception e) {
			throw new TermSuiteException(e);
		}
	}
}

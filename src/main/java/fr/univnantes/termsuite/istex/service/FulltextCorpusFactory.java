package fr.univnantes.termsuite.istex.service;

import java.util.List;

import fr.univnantes.termsuite.istex.FulltextIstexCorpus;
import fr.univnantes.termsuite.model.Lang;

public interface FulltextCorpusFactory {
	public FulltextIstexCorpus createCorpus(Lang lang, List<String> documentIds);
}

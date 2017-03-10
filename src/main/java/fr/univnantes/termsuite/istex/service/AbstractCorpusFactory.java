package fr.univnantes.termsuite.istex.service;

import java.util.List;

import fr.univnantes.termsuite.istex.AbstractIstexCorpus;
import fr.univnantes.termsuite.model.Lang;

public interface AbstractCorpusFactory {
	public AbstractIstexCorpus createCorpus(Lang lang, List<String> documentIds);
}

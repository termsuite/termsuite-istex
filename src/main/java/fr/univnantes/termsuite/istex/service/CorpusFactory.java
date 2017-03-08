package fr.univnantes.termsuite.istex.service;

import java.util.List;

import fr.univnantes.termsuite.istex.IstexCorpus;
import fr.univnantes.termsuite.model.Lang;

public interface CorpusFactory {

	public IstexCorpus create(Lang lang, List<String> documentIds);
}

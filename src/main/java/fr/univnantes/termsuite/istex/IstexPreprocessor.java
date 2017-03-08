package fr.univnantes.termsuite.istex;

import com.google.common.base.Preconditions;

import fr.univnantes.termsuite.api.Preprocessor;
import fr.univnantes.termsuite.model.IndexedCorpus;

public class IstexPreprocessor {
	
	private Preprocessor preprocessor;
	
	public IstexPreprocessor(Preprocessor preprocessor) {
		super();
		this.preprocessor = preprocessor;
	}
	
	public IndexedCorpus toIndexedCorpus(IstexCorpus corpus) {
		checkIds(corpus);
		return this.preprocessor.toIndexedCorpus(corpus, 500000);
	}

	private void checkIds(IstexCorpus corpus) {
		Preconditions.checkArgument(corpus.getDocumentIds() != null, "Istex document ids not set");
		Preconditions.checkArgument(!corpus.getDocumentIds().isEmpty(), "Istex document ids must not be empty");
	}
}

package fr.univnantes.termsuite.istex.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univnantes.termsuite.api.Preprocessor;
import fr.univnantes.termsuite.istex.IstexCorpus;
import fr.univnantes.termsuite.istex.IstexLauncherException;
import fr.univnantes.termsuite.istex.IstexPreprocessor;
import fr.univnantes.termsuite.istex.TermSuiteIstex;
import fr.univnantes.termsuite.model.IndexedCorpus;
import fr.univnantes.termsuite.model.Lang;
import fr.univnantes.termsuite.model.Tagger;
import fr.univnantes.termsuite.tools.TermSuiteCliException;
import fr.univnantes.termsuite.tools.TerminologyExtractorCLI;
import fr.univnantes.termsuite.tools.opt.CliOption;
import fr.univnantes.termsuite.tools.opt.TermSuiteCliOption;

public class IstexLauncher extends TerminologyExtractorCLI {

	private static final Logger LOGGER = LoggerFactory.getLogger(IstexLauncher.class);
	
	public IstexLauncher() {
		super("Extracts terminology from a collection of ISTEX documents");
	}

	@Override
	public void configureOpts() {
		super.configureOpts();
		
		/*
		 * Remove input corpus option
		 */
		Set<CliOption> corpusOpts = new HashSet<>();
		corpusOpts.add(TermSuiteCliOption.FROM_TXT_CORPUS_PATH);
		corpusOpts.add(TermSuiteCliOption.FROM_PREPARED_CORPUS_PATH);

		if(!exactlyOneOfBags.remove(corpusOpts))
			LOGGER.warn("Could not remove option bag {}", corpusOpts);
		declaredOptions.remove(TermSuiteCliOption.FROM_TXT_CORPUS_PATH);
		declaredOptions.remove(TermSuiteCliOption.FROM_PREPARED_CORPUS_PATH);
		facultativeOptions.remove(TermSuiteCliOption.FROM_TXT_CORPUS_PATH);
		facultativeOptions.remove(TermSuiteCliOption.FROM_PREPARED_CORPUS_PATH);

		/*
		 * Set option TAGGER_PATH mandatory (no more conditional on FROM_TXT_CORPUS_PATH)
		 */
		conditionalOptions.remove(TermSuiteCliOption.FROM_TXT_CORPUS_PATH);
		mandatoryOptions.add(TermSuiteCliOption.TAGGER_PATH);
		
		/*
		 * Declare ISTEX corpus options
		 */
		declareExactlyOneOf(
				IstexCliOption.ID_FILE, 
				IstexCliOption.DOCUMENT_IDS);
	}
	
	@Override
	protected IndexedCorpus getIndexedCorpus() {
		Preprocessor preprocessor = TermSuiteIstex.preprocessor()
				.setTaggerPath(asPath(TermSuiteCliOption.TAGGER_PATH));
	
		if(isSet(TermSuiteCliOption.TAGGER))
			preprocessor.setTagger(Tagger.forName(asString(TermSuiteCliOption.TAGGER)));
		
		if(clientHelper.getHistory().isPresent())
			preprocessor.setHistory(clientHelper.getHistory().get());
		
		preprocessor.setResourceOptions(clientHelper.getResourceConfig());
		
		IstexPreprocessor istexPreprocessor = new IstexPreprocessor(preprocessor);
		
		IstexCorpus textCorpus = getIstexCorpus();
		
		IndexedCorpus indexedCorpus = istexPreprocessor.toIndexedCorpus(textCorpus);
		return indexedCorpus;
	}

	private IstexCorpus getIstexCorpus() {
		Lang lang = getLang();
		List<String> documentIds = getDocumentIds();
		IstexCorpus textCorpus = TermSuiteIstex.createIstexCorpus(lang, documentIds);
		LOGGER.info("Istex corpus - language: " + textCorpus.getLang());
		LOGGER.info("Istex corpus - num docs: " + (textCorpus.getDocumentIds() == null ? 0 : textCorpus.getDocumentIds().size()));
		return textCorpus;
	}

	@Override
	public Lang getLang() {
		if(isSet(TermSuiteCliOption.LANGUAGE))
			return Lang.forName(asString(TermSuiteCliOption.LANGUAGE));
		else
			throw new IstexLauncherException(String.format("Option -%s [--%s] missing", TermSuiteCliOption.LANGUAGE.getOptShortName(), TermSuiteCliOption.LANGUAGE.getOptName()));
	}
	
	private List<String> getDocumentIds() {
		List<String> ids;
		if(isSet(IstexCliOption.DOCUMENT_IDS)) 
			 ids = asList(IstexCliOption.DOCUMENT_IDS);
		else {
			try {
				Path isFile = asPath(IstexCliOption.ID_FILE);
				ids = TermSuiteIstex.readDocumentIds(isFile);
			} catch (IOException e) {
				throw new TermSuiteCliException("Could not read ids from file " + asString(IstexCliOption.ID_FILE), e);
			}
		}
		
		return ids;
	}


	public static void main(String[] args) {
		new IstexLauncher().launch(args);
	}
}
